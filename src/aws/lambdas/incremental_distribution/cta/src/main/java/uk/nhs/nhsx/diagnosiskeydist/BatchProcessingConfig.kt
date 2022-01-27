package uk.nhs.nhsx.diagnosiskeydist

import uk.nhs.nhsx.core.Environment
import uk.nhs.nhsx.core.Environment.EnvironmentKey
import uk.nhs.nhsx.core.Environment.EnvironmentKey.Companion.bool
import uk.nhs.nhsx.core.Environment.EnvironmentKey.Companion.string
import uk.nhs.nhsx.core.Environment.EnvironmentKey.Companion.value
import uk.nhs.nhsx.core.aws.s3.BucketName
import uk.nhs.nhsx.core.aws.ssm.ParameterName
import java.time.Duration

data class BatchProcessingConfig(
    val shouldAbortOutsideTimeWindow: Boolean,
    val zipBucketName: BucketName,
    val cloudFrontDistributionId: String,
    val distributionPatternDaily: String,
    val distributionPattern2Hourly: String,
    val ssmAGSigningKeyParameterName: ParameterName,
    val ssmMetaDataSigningKeyParameterName: ParameterName,
    val zipSubmissionPeriodOffset: Duration,
    val loadSubmissionsThreadPoolSize: Int,
    val loadSubmissionsTimeout: Duration,
    val maximalZipSignS3PutTime: Duration
) {
    companion object {
        private val ABORT_OUTSIDE_TIME_WINDOW = bool("ABORT_OUTSIDE_TIME_WINDOW")
        private val DISTRIBUTION_BUCKET_NAME = value("DISTRIBUTION_BUCKET_NAME", BucketName)
        private val DISTRIBUTION_ID = string("DISTRIBUTION_ID")
        private val DISTRIBUTION_PATTERN_DAILY = string("DISTRIBUTION_PATTERN_DAILY")
        private val DISTRIBUTION_PATTERN_2HOURLY = string("DISTRIBUTION_PATTERN_2HOURLY")
        private val SSM_AG_SIGNING_KEY_ID_PARAMETER_NAME = value("SSM_AG_SIGNING_KEY_ID_PARAMETER_NAME", ParameterName)
        private val SSM_METADATA_SIGNING_KEY_ID_PARAMETER_NAME = value("SSM_METADATA_SIGNING_KEY_ID_PARAMETER_NAME", ParameterName)
        private val ZIP_SUBMISSION_PERIOD_OFFSET = EnvironmentKey.duration("ZIP_SUBMISSION_PERIOD_OFFSET")
        private val LOAD_SUBMISSIONS_TIMEOUT = EnvironmentKey.duration("LOAD_SUBMISSIONS_TIMEOUT")
        private val LOAD_SUBMISSIONS_THREAD_POOL_SIZE = EnvironmentKey.integer("LOAD_SUBMISSIONS_THREAD_POOL_SIZE")
        private val MAXIMAL_ZIP_SIGN_S3_PUT_TIME = EnvironmentKey.duration("MAXIMAL_ZIP_SIGN_S3_PUT_TIME")

        fun fromEnvironment(e: Environment) = BatchProcessingConfig(
            e.access.required(ABORT_OUTSIDE_TIME_WINDOW),
            e.access.required(DISTRIBUTION_BUCKET_NAME),
            e.access.required(DISTRIBUTION_ID),
            e.access.required(DISTRIBUTION_PATTERN_DAILY),
            e.access.required(DISTRIBUTION_PATTERN_2HOURLY),
            e.access.required(SSM_AG_SIGNING_KEY_ID_PARAMETER_NAME),
            e.access.required(SSM_METADATA_SIGNING_KEY_ID_PARAMETER_NAME),
            e.access.defaulted(ZIP_SUBMISSION_PERIOD_OFFSET) { Duration.ofMinutes(-15) },
            e.access.defaulted(LOAD_SUBMISSIONS_THREAD_POOL_SIZE) { 15 },
            e.access.defaulted(LOAD_SUBMISSIONS_TIMEOUT) { Duration.ofMinutes(10) },
            e.access.defaulted(MAXIMAL_ZIP_SIGN_S3_PUT_TIME) { Duration.ofMinutes(2) }
        )
    }
}
