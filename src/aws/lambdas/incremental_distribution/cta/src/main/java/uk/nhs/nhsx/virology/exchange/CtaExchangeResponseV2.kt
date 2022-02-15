package uk.nhs.nhsx.virology.exchange

import uk.nhs.nhsx.domain.DiagnosisKeySubmissionToken
import uk.nhs.nhsx.domain.TestEndDate
import uk.nhs.nhsx.domain.TestKit
import uk.nhs.nhsx.domain.TestResult
import uk.nhs.nhsx.virology.domain.TestInstructionsValidator.validateOrThrow

data class CtaExchangeResponseV2(
    val diagnosisKeySubmissionToken: DiagnosisKeySubmissionToken,
    val testResult: TestResult,
    val testEndDate: TestEndDate,
    val testKit: TestKit,
    val diagnosisKeySubmissionSupported: Boolean,
    val requiresConfirmatoryTest: Boolean,
    val confirmatoryDayLimit: Int?,
    val shouldOfferFollowUpTest: Boolean,
    val venueHistorySharingSupported: Boolean = false
) {
    init {
        validateOrThrow(this)
    }
}
