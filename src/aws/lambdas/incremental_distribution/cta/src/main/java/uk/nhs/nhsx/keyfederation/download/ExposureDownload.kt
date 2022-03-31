package uk.nhs.nhsx.keyfederation.download

import uk.nhs.nhsx.domain.ReportType
import uk.nhs.nhsx.domain.TestType

data class ExposureDownload(
    val keyData: String,
    val rollingStartNumber: Int,
    val transmissionRiskLevel: Int,
    val rollingPeriod: Int,
    val origin: String,
    val regions: List<String>,
    val testType: TestType,
    val reportType: ReportType,
    val daysSinceOnset: Int
)

