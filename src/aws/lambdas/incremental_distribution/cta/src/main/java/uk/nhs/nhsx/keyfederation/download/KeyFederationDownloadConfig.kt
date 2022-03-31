package uk.nhs.nhsx.keyfederation.download

import uk.nhs.nhsx.core.Environment
import uk.nhs.nhsx.core.Environment.Companion.WORKSPACE
import uk.nhs.nhsx.core.Environment.EnvironmentKey
import uk.nhs.nhsx.core.FeatureFlag
import uk.nhs.nhsx.core.StandardSigning
import uk.nhs.nhsx.core.WorkspaceFeatureFlag
import uk.nhs.nhsx.core.aws.dynamodb.TableName
import uk.nhs.nhsx.core.aws.s3.BucketName
import uk.nhs.nhsx.core.aws.secretsmanager.SecretName
import uk.nhs.nhsx.core.aws.ssm.ParameterName

class KeyFederationDownloadConfig(
    val maxSubsequentBatchDownloadCount: Int,
    val initialDownloadHistoryDays: Int,
    val downloadFeatureFlag: FeatureFlag,
    val downloadRiskLevelDefaultEnabled: Boolean,
    val downloadRiskLevelDefault: Int,
    val submissionBucketName: BucketName,
    val interopBaseUrl: String,
    val interopAuthTokenSecretName: SecretName,
    val signingKeyParameterName: ParameterName,
    val federatedKeyDownloadPrefix: String,
    val stateTableName: TableName,
    val validOrigins: List<String>
) {
    companion object {
        private val MAX_SUBSEQUENT_BATCH_DOWNLOAD_COUNT = EnvironmentKey.integer("MAX_SUBSEQUENT_BATCH_DOWNLOAD_COUNT")
        private val INITIAL_DOWNLOAD_HISTORY_DAYS = EnvironmentKey.integer("INITIAL_DOWNLOAD_HISTORY_DAYS")
        private val DOWNLOAD_ENABLED_WORKSPACES = EnvironmentKey.strings("DOWNLOAD_ENABLED_WORKSPACES")
        private val DOWNLOAD_RISK_LEVEL_DEFAULT_ENABLED = EnvironmentKey.bool("DOWNLOAD_RISK_LEVEL_DEFAULT_ENABLED")
        private val DOWNLOAD_RISK_LEVEL_DEFAULT = EnvironmentKey.integer("DOWNLOAD_RISK_LEVEL_DEFAULT")
        private val SUBMISSION_BUCKET_NAME = EnvironmentKey.value("SUBMISSION_BUCKET_NAME", BucketName)
        private val INTEROP_BASE_URL = EnvironmentKey.string("INTEROP_BASE_URL")
        private val INTEROP_AUTH_TOKEN_SECRET_NAME = EnvironmentKey.value("INTEROP_AUTH_TOKEN_SECRET_NAME", SecretName)
        private val FEDERATED_KEY_DOWNLOAD_PREFIX = EnvironmentKey.string("FEDERATED_KEY_DOWNLOAD_PREFIX")
        private val PROCESSOR_STATE_TABLE = EnvironmentKey.value("PROCESSOR_STATE_TABLE", TableName)
        private val VALID_DOWNLOAD_ORIGINS = EnvironmentKey.strings("VALID_DOWNLOAD_ORIGINS")

        fun fromEnvironment(e: Environment) = KeyFederationDownloadConfig(
            maxSubsequentBatchDownloadCount = e.access.required(MAX_SUBSEQUENT_BATCH_DOWNLOAD_COUNT),
            initialDownloadHistoryDays = e.access.required(INITIAL_DOWNLOAD_HISTORY_DAYS),
            downloadFeatureFlag = WorkspaceFeatureFlag(e.access.required(WORKSPACE), e.access.required(DOWNLOAD_ENABLED_WORKSPACES)),
            downloadRiskLevelDefaultEnabled = e.access.required(DOWNLOAD_RISK_LEVEL_DEFAULT_ENABLED),
            downloadRiskLevelDefault = e.access.required(DOWNLOAD_RISK_LEVEL_DEFAULT),
            submissionBucketName = e.access.required(SUBMISSION_BUCKET_NAME),
            interopBaseUrl = e.access.required(INTEROP_BASE_URL),
            interopAuthTokenSecretName = e.access.required(INTEROP_AUTH_TOKEN_SECRET_NAME),
            signingKeyParameterName = e.access.required(StandardSigning.SSM_KEY_ID_PARAMETER_NAME),
            federatedKeyDownloadPrefix = e.access.required(FEDERATED_KEY_DOWNLOAD_PREFIX),
            stateTableName = e.access.required(PROCESSOR_STATE_TABLE),
            validOrigins = e.access.required(VALID_DOWNLOAD_ORIGINS),
        )
    }
}
