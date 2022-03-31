package uk.nhs.nhsx.aae

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.s3.model.S3Object
import uk.nhs.nhsx.core.Json.readJsonOrThrow
import uk.nhs.nhsx.core.aws.s3.AwsS3
import uk.nhs.nhsx.core.aws.s3.Locator
import uk.nhs.nhsx.core.aws.s3.ObjectKey
import uk.nhs.nhsx.core.events.Event
import uk.nhs.nhsx.core.events.ExceptionThrown
import java.util.regex.Pattern

/**
 * S3 PutObject -> CloudTrail -> EventBridge rule & transformation -> SQS -> Lambda: Upload of S3 object (e.g. JSON, Parquet) to Export Destination via HTTPS PUT
 */
class AAEFileExporter constructor(
    private val s3Client: AwsS3,
    private val uploader: AAEUploader,
    private val config: AAEUploadConfig
) {

    fun export(input: SQSEvent): Event {
        if (input.records.size != 1)
            return ExceptionThrown<RuntimeException>(IllegalStateException(".tf configuration error: batch_size != 1"))

        val sqsMessage = input.records[0]
        val event: TransformedS3PutObjectCloudTrailEvent
        try {
            event = readJsonOrThrow(sqsMessage.body)
        } catch (e: Exception) { // -> no retry
            return ExceptionThrown(
                RuntimeException(
                    """SQS message parsing failed (no retry): sqsMessage.id=${sqsMessage.messageId}, body=${sqsMessage.body}""",
                    e
                )
            )
        }

        if (event.bucketName.value == config.analyticsEventsBucket)
            return uploadToAAE(event, sqsMessage)

        if (event.bucketName.value == config.analyticsDataBucket)
            return when {
                event.key.value.startsWith(FORMAT_CONVERSION_FAILED_PREFIX) -> {
                    S3ToParquetObjectConversionFailure(sqsMessage.messageId, event.bucketName, event.key)
                }
                !startsWithDisallowedPrefix(event.key) -> {
                    S3ObjectStartsWithDisallowedPrefix(
                        sqsMessage.messageId,
                        event.bucketName,
                        event.key
                    ) // [PARQUET CONSOLIDATION] ignore temporary files
                }
                !isCreatedByParquet(event.key) -> {
                    S3ParquetFileNotCreatedByFirehouse(sqsMessage.messageId, event.bucketName, event.key)
                }
                else -> {
                    uploadToAAE(event, sqsMessage)
                }
            }

        return ExceptionThrown<RuntimeException>(IllegalStateException("cannot handle payload from bucket ${event.bucketName}"))

    }

    private fun uploadToAAE(
        event: TransformedS3PutObjectCloudTrailEvent,
        sqsMessage: SQSEvent.SQSMessage
    ) = try {
        when (val s3Object = s3Client.getObject(Locator.of(event.bucketName, event.key))) {
            null -> S3ObjectNotFound(sqsMessage.messageId, event.bucketName, event.key)
            else -> {
                uploader.uploadFile(
                    getFilename(s3Object),
                    getContent(s3Object),
                    s3Object.objectMetadata.contentType
                )
                DataUploadedToAAE(sqsMessage.messageId, event.bucketName, event.key)
            }
        }
    } catch (e: Exception) { // -> retry o
        val message = with(sqsMessage) {
            """S3 object NOT uploaded (retry candidate): sqsMessage.id=${messageId}, body=${body}"""
        }
        throw RuntimeException(message, e) // r DLQ
    }

    private fun startsWithDisallowedPrefix(key: ObjectKey): Boolean {
        var isAllowed = true

        if (config.s3DisallowedPrefixList.isNotEmpty()) {
            for (disallowedPrefix in config.s3DisallowedPrefixList.split(",").toTypedArray()) {
                isAllowed = isAllowed and !key.value.startsWith(disallowedPrefix)
            }
        }
        return isAllowed
    }

    private fun isCreatedByParquet(key: ObjectKey): Boolean =
        FILE_CREATED_BY_PARQUET_REGEX.matcher(key.value).matches()

    private fun getContent(obj: S3Object): ByteArray {
        obj.objectContent.use { `in` -> return `in`.readAllBytes() }
    }

    private fun getFilename(obj: S3Object): String =
        if (obj.key.lastIndexOf("/") != -1) obj.key.substring(obj.key.lastIndexOf("/") + 1) else obj.key


    companion object {
        private const val FORMAT_CONVERSION_FAILED_PREFIX = "format-conversion-failed/"

        // Example file created by parquet - 2022/05/18/04/te-staging-analytics-6-2021-05-12-12-30-21-c81ba8a5-d040-4398-a173-ff4569cdd24a.parquet
        private val FILE_CREATED_BY_PARQUET_REGEX =
            Pattern.compile("""(.+)/te-(.+)-analytics-(\d)+-(\d{4})-(\d{2})-(\d{2})-(\d{2})-(\d{2})-(\d{2})-([A-Za-z0-9]+)-([A-Za-z0-9]+)-([A-Za-z0-9]+)-([A-Za-z0-9]+)-([A-Za-z0-9]+)\.parquet""")
    }

}
