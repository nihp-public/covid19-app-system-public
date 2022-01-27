package uk.nhs.nhsx.analyticssubmission

import uk.nhs.nhsx.core.Json
import java.time.Instant

fun analyticsSubmissionAndroid(
    localAuthority: String? = null,
    startDate: String = "2020-07-27T23:00:00Z",
    endDate: String = "2020-07-28T22:59:00Z"
) = analyticsSubmissionPayload(
    operatingSystemVersion = "29",
    deviceModel = "Pixel 4XL",
    postDistrict = "AB10",
    localAuthority = localAuthority,
    startDate = startDate,
    endDate = endDate,
    cumulativeDownloadBytes = 140000000,
    cumulativeUploadBytes = 160000000
)

fun analyticsSubmissionIos(
    localAuthority: String? = null,
    startDate: String = "2020-07-27T23:00:00Z",
    endDate: String = "2020-07-28T22:59:00Z"
) = analyticsSubmissionPayload(
    operatingSystemVersion = "iPhone OS 13.5.1 (17F80)",
    deviceModel = "iPhone11,2",
    postDistrict = "AB10",
    localAuthority = localAuthority,
    startDate = startDate,
    endDate = endDate,
    cumulativeCellularDownloadBytes = 80000000,
    cumulativeCellularUploadBytes = 70000000,
    cumulativeWifiDownloadBytes = 60000000,
    cumulativeWifiUploadBytes = 50000000,
)

fun analyticsSubmissionIosComplete(
    deviceModel: String = "iPhone11,2",
    postalDistrict: String = "AB10",
    localAuthority: String? = null,
    startDate: String = "2020-07-27T23:00:00Z",
    endDate: String = "2020-07-28T22:59:00Z",
    useCounter: Boolean = false
) = analyticsSubmissionPayload(
    operatingSystemVersion = "iPhone OS 13.5.1 (17F80)",
    deviceModel = deviceModel,
    postDistrict = postalDistrict,
    localAuthority = localAuthority,
    startDate = startDate,
    endDate = endDate,
    cumulativeCellularDownloadBytes = 80000000,
    cumulativeCellularUploadBytes = 70000000,
    cumulativeWifiDownloadBytes = 60000000,
    cumulativeWifiUploadBytes = 50000000,
    additionalMetrics = true,
    useCounter = useCounter
)

fun analyticsSubmissionAndroidComplete(
    deviceModel: String = "Pixel 4XL",
    postalDistrict: String = "AB10",
    localAuthority: String? = null,
    startDate: String = "2020-07-27T23:00:00Z",
    endDate: String = "2020-07-28T22:59:00Z",
    useCounter: Boolean = false,
) = analyticsSubmissionPayload(
    operatingSystemVersion = "29",
    deviceModel = deviceModel,
    postDistrict = postalDistrict,
    localAuthority = localAuthority,
    startDate = startDate,
    endDate = endDate,
    cumulativeDownloadBytes = 140000000,
    cumulativeUploadBytes = 160000000,
    additionalMetrics = true,
    useCounter = useCounter
)

private fun analyticsSubmissionPayload(
    operatingSystemVersion: String,
    deviceModel: String,
    postDistrict: String,
    localAuthority: String? = null,
    startDate: String,
    endDate: String,
    cumulativeDownloadBytes: Long? = null,
    cumulativeUploadBytes: Long? = null,
    cumulativeCellularDownloadBytes: Long? = null,
    cumulativeCellularUploadBytes: Long? = null,
    cumulativeWifiDownloadBytes: Long? = null,
    cumulativeWifiUploadBytes: Long? = null,
    additionalMetrics: Boolean = false,
    useCounter: Boolean = false
): String {

    val metadata = mutableMapOf(
        "operatingSystemVersion" to operatingSystemVersion,
        "latestApplicationVersion" to "3.0",
        "deviceModel" to deviceModel,
        "postalDistrict" to postDistrict
    )

    if (localAuthority != null) metadata["localAuthority"] = localAuthority

    val analyticsWindow = mutableMapOf(
        "startDate" to startDate,
        "endDate" to endDate
    )

    var counter = 1
    val metrics: MutableMap<String, Any?> = mutableMapOf(
        "cumulativeDownloadBytes" to if (useCounter) counter++ else cumulativeDownloadBytes,
        "cumulativeUploadBytes" to if (useCounter) counter++ else cumulativeUploadBytes,
        "cumulativeCellularDownloadBytes" to if (useCounter) counter++ else cumulativeCellularDownloadBytes,
        "cumulativeCellularUploadBytes" to if (useCounter) counter++ else cumulativeCellularUploadBytes,
        "cumulativeWifiDownloadBytes" to if (useCounter) counter++ else cumulativeWifiDownloadBytes,
        "cumulativeWifiUploadBytes" to if (useCounter) counter++ else cumulativeWifiUploadBytes,
        "checkedIn" to if (useCounter) counter++ else 1,
        "canceledCheckIn" to if (useCounter) counter++ else 1,
        "receivedVoidTestResult" to if (useCounter) counter++ else 1,
        "isIsolatingBackgroundTick" to if (useCounter) counter++ else 1,
        "hasHadRiskyContactBackgroundTick" to if (useCounter) counter++ else 1,
        "receivedPositiveTestResult" to if (useCounter) counter++ else 1,
        "receivedNegativeTestResult" to if (useCounter) counter++ else 1,
        "hasSelfDiagnosedPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "completedQuestionnaireAndStartedIsolation" to if (useCounter) counter++ else 1,
        "encounterDetectionPausedBackgroundTick" to if (useCounter) counter++ else 1,
        "completedQuestionnaireButDidNotStartIsolation" to if (useCounter) counter++ else 1,
        "totalBackgroundTasks" to if (useCounter) counter++ else 1,
        "runningNormallyBackgroundTick" to if (useCounter) counter++ else 1,
        "completedOnboarding" to if (useCounter) counter++ else 1
    )

    if (additionalMetrics) {
        metrics.putAll(additionalMetrics(counterValue = counter, useCounter = true))
    }

    val submissionPayload = mapOf(
        "metadata" to metadata,
        "analyticsWindow" to analyticsWindow,
        "metrics" to metrics,
        "includesMultipleApplicationVersions" to false
    )
    return Json.toJson(submissionPayload)
}

private fun additionalMetrics(counterValue: Int,
                              useCounter: Boolean): Map<String, Any> {
    var counter = counterValue
    return mapOf(
        "receivedVoidTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedPositiveTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedNegativeTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedVoidTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedPositiveTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedNegativeTestResultViaPolling" to if (useCounter) counter++ else 1,
        "hasSelfDiagnosedBackgroundTick" to if (useCounter) counter++ else 1,
        "hasTestedPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "isIsolatingForSelfDiagnosedBackgroundTick" to if (useCounter) counter++ else 1,
        "isIsolatingForTestedPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "isIsolatingForHadRiskyContactBackgroundTick" to if (useCounter) counter++ else 1,
        "receivedRiskyContactNotification" to if (useCounter) counter++ else 1,
        "startedIsolation" to if (useCounter) counter++ else 1,
        "receivedPositiveTestResultWhenIsolatingDueToRiskyContact" to if (useCounter) counter++ else 1,
        "receivedActiveIpcToken" to if (useCounter) counter++ else 1,
        "haveActiveIpcTokenBackgroundTick" to if (useCounter) counter++ else 1,
        "selectedIsolationPaymentsButton" to if (useCounter) counter++ else 1,
        "launchedIsolationPaymentsApplication" to if (useCounter) counter++ else 1,
        "receivedPositiveLFDTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedNegativeLFDTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedVoidLFDTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedPositiveLFDTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedNegativeLFDTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedVoidLFDTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "hasTestedLFDPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "isIsolatingForTestedLFDPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "totalExposureWindowsNotConsideredRisky" to if (useCounter) counter++ else 1,
        "totalExposureWindowsConsideredRisky" to if (useCounter) counter++ else 1,
        "acknowledgedStartOfIsolationDueToRiskyContact" to if (useCounter) counter++ else 1,
        "hasRiskyContactNotificationsEnabledBackgroundTick" to if (useCounter) counter++ else 1,
        "totalRiskyContactReminderNotifications" to if (useCounter) counter++ else 1,
        "receivedUnconfirmedPositiveTestResult" to if (useCounter) counter++ else 1,
        "isIsolatingForUnconfirmedTestBackgroundTick" to if (useCounter) counter++ else 1,
        "launchedTestOrdering" to if (useCounter) counter++ else 1,
        "didHaveSymptomsBeforeReceivedTestResult" to if (useCounter) counter++ else 1,
        "didRememberOnsetSymptomsDateBeforeReceivedTestResult" to if (useCounter) counter++ else 1,
        "didAskForSymptomsOnPositiveTestEntry" to if (useCounter) counter++ else 1,
        "declaredNegativeResultFromDCT" to if (useCounter) counter++ else 1,
        "receivedPositiveSelfRapidTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedNegativeSelfRapidTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedVoidSelfRapidTestResultViaPolling" to if (useCounter) counter++ else 1,
        "receivedPositiveSelfRapidTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedNegativeSelfRapidTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "receivedVoidSelfRapidTestResultEnteredManually" to if (useCounter) counter++ else 1,
        "isIsolatingForTestedSelfRapidPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "hasTestedSelfRapidPositiveBackgroundTick" to if (useCounter) counter++ else 1,
        "receivedRiskyVenueM1Warning" to if (useCounter) counter++ else 1,
        "receivedRiskyVenueM2Warning" to if (useCounter) counter++ else 1,
        "hasReceivedRiskyVenueM2WarningBackgroundTick" to if (useCounter) counter++ else 1,
        "totalAlarmManagerBackgroundTasks" to if (useCounter) counter++ else 1,
        "missingPacketsLast7Days" to if (useCounter) counter++ else 1,
        "consentedToShareVenueHistory" to if (useCounter) counter++ else 1,
        "askedToShareVenueHistory" to if (useCounter) counter++ else 1,
        "askedToShareExposureKeysInTheInitialFlow" to if (useCounter) counter++ else 1,
        "consentedToShareExposureKeysInTheInitialFlow" to if (useCounter) counter++ else 1,
        "totalShareExposureKeysReminderNotifications" to if (useCounter) counter++ else 1,
        "consentedToShareExposureKeysInReminderScreen" to if (useCounter) counter++ else 1,
        "successfullySharedExposureKeys" to if (useCounter) counter++ else 1,
        "didSendLocalInfoNotification" to if (useCounter) counter++ else 1,
        "didAccessLocalInfoScreenViaNotification" to if (useCounter) counter++ else 1,
        "didAccessLocalInfoScreenViaBanner" to if (useCounter) counter++ else 1,
        "isDisplayingLocalInfoBackgroundTick" to if (useCounter) counter++ else 1,
        "positiveLabResultAfterPositiveLFD" to if (useCounter) counter++ else 1,
        "negativeLabResultAfterPositiveLFDWithinTimeLimit" to if (useCounter) counter++ else 1,
        "negativeLabResultAfterPositiveLFDOutsideTimeLimit" to if (useCounter) counter++ else 1,
        "positiveLabResultAfterPositiveSelfRapidTest" to if (useCounter) counter++ else 1,
        "negativeLabResultAfterPositiveSelfRapidTestWithinTimeLimit" to if (useCounter) counter++ else 1,
        "negativeLabResultAfterPositiveSelfRapidTestOutsideTimeLimit" to if (useCounter) counter++ else 1,
        "didAccessRiskyVenueM2Notification" to if (useCounter) counter++ else 1,
        "selectedTakeTestM2Journey" to if (useCounter) counter++ else 1,
        "selectedTakeTestLaterM2Journey" to if (useCounter) counter++ else 1,
        "selectedHasSymptomsM2Journey" to if (useCounter) counter++ else 1,
        "selectedHasNoSymptomsM2Journey" to if (useCounter) counter++ else 1,
        "selectedLFDTestOrderingM2Journey" to if (useCounter) counter++ else 1,
        "selectedHasLFDTestM2Journey" to if (useCounter) counter++ else 1,
        "optedOutForContactIsolation" to if (useCounter) counter++ else 1,
        "optedOutForContactIsolationBackgroundTick" to if (useCounter) counter++ else 1,
        "appIsUsableBackgroundTick" to if (useCounter) counter++ else 1,
        "appIsContactTraceableBackgroundTick" to if (useCounter) counter++ else 1,
        "didAccessSelfIsolationNoteLink" to if (useCounter) counter++ else 1,
        "appIsUsableBluetoothOffBackgroundTick" to if (useCounter) counter else 1
    )
}

fun analyticsStoredPayload(
    eventStartDate: Instant,
    eventEndDate: Instant,
    postalDistrict: String,
    deviceModel: String = "",
    operatingSystemVersion: String = "",
    latestApplicationVersion: String = "",
    localAuthority: String?,
    cumulativeDownloadBytes: Int? = null,
    cumulativeUploadBytes: Int? = null,
    cumulativeCellularDownloadBytes: Int? = null,
    cumulativeCellularUploadBytes: Int? = null,
    cumulativeWifiDownloadBytes: Int? = null,
    cumulativeWifiUploadBytes: Int? = null,
    checkedIn: Int = 0,
    canceledCheckIn: Int = 0,
    receivedVoidTestResult: Int = 0,
    isIsolatingBackgroundTick: Int = 0,
    hasHadRiskyContactBackgroundTick: Int = 0,
    receivedPositiveTestResult: Int = 0,
    receivedNegativeTestResult: Int = 0,
    hasSelfDiagnosedPositiveBackgroundTick: Int = 0,
    completedQuestionnaireAndStartedIsolation: Int = 0,
    encounterDetectionPausedBackgroundTick: Int = 0,
    completedQuestionnaireButDidNotStartIsolation: Int = 0,
    totalBackgroundTasks: Int = 0,
    runningNormallyBackgroundTick: Int = 0,
    completedOnboarding: Int = 0,
    includesMultipleApplicationVersions: Boolean,
    receivedVoidTestResultEnteredManually: Int? = null,
    receivedPositiveTestResultEnteredManually: Int? = null,
    receivedNegativeTestResultEnteredManually: Int? = null,
    receivedVoidTestResultViaPolling: Int? = null,
    receivedPositiveTestResultViaPolling: Int? = null,
    receivedNegativeTestResultViaPolling: Int? = null,
    hasSelfDiagnosedBackgroundTick: Int? = null,
    hasTestedPositiveBackgroundTick: Int? = null,
    isIsolatingForSelfDiagnosedBackgroundTick: Int? = null,
    isIsolatingForTestedPositiveBackgroundTick: Int? = null,
    isIsolatingForHadRiskyContactBackgroundTick: Int? = null,
    receivedRiskyContactNotification: Int? = null,
    startedIsolation: Int? = null,
    receivedPositiveTestResultWhenIsolatingDueToRiskyContact: Int? = null,
    receivedActiveIpcToken: Int? = null,
    haveActiveIpcTokenBackgroundTick: Int? = null,
    selectedIsolationPaymentsButton: Int? = null,
    launchedIsolationPaymentsApplication: Int? = null,
    receivedPositiveLFDTestResultViaPolling: Int? = null,
    receivedNegativeLFDTestResultViaPolling: Int? = null,
    receivedVoidLFDTestResultViaPolling: Int? = null,
    receivedPositiveLFDTestResultEnteredManually: Int? = null,
    receivedNegativeLFDTestResultEnteredManually: Int? = null,
    receivedVoidLFDTestResultEnteredManually: Int? = null,
    hasTestedLFDPositiveBackgroundTick: Int? = null,
    isIsolatingForTestedLFDPositiveBackgroundTick: Int? = null,
    totalExposureWindowsNotConsideredRisky: Int? = null,
    totalExposureWindowsConsideredRisky: Int? = null,
    acknowledgedStartOfIsolationDueToRiskyContact: Int? = null,
    hasRiskyContactNotificationsEnabledBackgroundTick: Int? = null,
    totalRiskyContactReminderNotifications: Int? = null,
    receivedUnconfirmedPositiveTestResult: Int? = null,
    isIsolatingForUnconfirmedTestBackgroundTick: Int? = null,
    launchedTestOrdering: Int? = null,
    didHaveSymptomsBeforeReceivedTestResult: Int? = null,
    didRememberOnsetSymptomsDateBeforeReceivedTestResult: Int? = null,
    didAskForSymptomsOnPositiveTestEntry: Int? = null,
    declaredNegativeResultFromDCT: Int? = null,
    receivedPositiveSelfRapidTestResultViaPolling: Int? = null,
    receivedNegativeSelfRapidTestResultViaPolling: Int? = null,
    receivedVoidSelfRapidTestResultViaPolling: Int? = null,
    receivedPositiveSelfRapidTestResultEnteredManually: Int? = null,
    receivedNegativeSelfRapidTestResultEnteredManually: Int? = null,
    receivedVoidSelfRapidTestResultEnteredManually: Int? = null,
    isIsolatingForTestedSelfRapidPositiveBackgroundTick: Int? = null,
    hasTestedSelfRapidPositiveBackgroundTick: Int? = null,
    receivedRiskyVenueM1Warning: Int? = null,
    receivedRiskyVenueM2Warning: Int? = null,
    hasReceivedRiskyVenueM2WarningBackgroundTick: Int? = null,
    totalAlarmManagerBackgroundTasks: Int? = null,
    missingPacketsLast7Days: Int? = null,
    consentedToShareVenueHistory: Int? = null,
    askedToShareVenueHistory: Int? = null,
    askedToShareExposureKeysInTheInitialFlow: Int? = null,
    consentedToShareExposureKeysInTheInitialFlow: Int? = null,
    totalShareExposureKeysReminderNotifications: Int? = null,
    consentedToShareExposureKeysInReminderScreen: Int? = null,
    successfullySharedExposureKeys: Int? = null,
    didSendLocalInfoNotification: Int? = null,
    didAccessLocalInfoScreenViaNotification: Int? = null,
    didAccessLocalInfoScreenViaBanner: Int? = null,
    isDisplayingLocalInfoBackgroundTick: Int? = null,
    positiveLabResultAfterPositiveLFD: Int? = null,
    negativeLabResultAfterPositiveLFDWithinTimeLimit: Int? = null,
    negativeLabResultAfterPositiveLFDOutsideTimeLimit: Int? = null,
    positiveLabResultAfterPositiveSelfRapidTest: Int? = null,
    negativeLabResultAfterPositiveSelfRapidTestWithinTimeLimit: Int? = null,
    negativeLabResultAfterPositiveSelfRapidTestOutsideTimeLimit: Int? = null,
    didAccessRiskyVenueM2Notification: Int? = null,
    selectedTakeTestM2Journey: Int? = null,
    selectedTakeTestLaterM2Journey: Int? = null,
    selectedHasSymptomsM2Journey: Int? = null,
    selectedHasNoSymptomsM2Journey: Int? = null,
    selectedLFDTestOrderingM2Journey: Int? = null,
    selectedHasLFDTestM2Journey: Int? = null,
    optedOutForContactIsolation: Int? = null,
    optedOutForContactIsolationBackgroundTick: Int? = null,
    appIsUsableBackgroundTick: Int? = null,
    appIsContactTraceableBackgroundTick: Int? = null,
    didAccessSelfIsolationNoteLink: Int? = null,
    appIsUsableBluetoothOffBackgroundTick: Int? = null
) = mapOf(
    "startDate" to "$eventStartDate",
    "endDate" to "$eventEndDate",
    "postalDistrict" to postalDistrict,
    "deviceModel" to deviceModel,
    "operatingSystemVersion" to operatingSystemVersion,
    "latestApplicationVersion" to latestApplicationVersion,
    "localAuthority" to localAuthority,
    "cumulativeDownloadBytes" to cumulativeDownloadBytes,
    "cumulativeUploadBytes" to cumulativeUploadBytes,
    "cumulativeCellularDownloadBytes" to cumulativeCellularDownloadBytes,
    "cumulativeCellularUploadBytes" to cumulativeCellularUploadBytes,
    "cumulativeWifiDownloadBytes" to cumulativeWifiDownloadBytes,
    "cumulativeWifiUploadBytes" to cumulativeWifiUploadBytes,
    "checkedIn" to checkedIn,
    "canceledCheckIn" to canceledCheckIn,
    "receivedVoidTestResult" to receivedVoidTestResult,
    "isIsolatingBackgroundTick" to isIsolatingBackgroundTick,
    "hasHadRiskyContactBackgroundTick" to hasHadRiskyContactBackgroundTick,
    "receivedPositiveTestResult" to receivedPositiveTestResult,
    "receivedNegativeTestResult" to receivedNegativeTestResult,
    "hasSelfDiagnosedPositiveBackgroundTick" to hasSelfDiagnosedPositiveBackgroundTick,
    "completedQuestionnaireAndStartedIsolation" to completedQuestionnaireAndStartedIsolation,
    "encounterDetectionPausedBackgroundTick" to encounterDetectionPausedBackgroundTick,
    "completedQuestionnaireButDidNotStartIsolation" to completedQuestionnaireButDidNotStartIsolation,
    "totalBackgroundTasks" to totalBackgroundTasks,
    "runningNormallyBackgroundTick" to runningNormallyBackgroundTick,
    "completedOnboarding" to completedOnboarding,
    "includesMultipleApplicationVersions" to includesMultipleApplicationVersions,
    "receivedVoidTestResultEnteredManually" to receivedVoidTestResultEnteredManually,
    "receivedPositiveTestResultEnteredManually" to receivedPositiveTestResultEnteredManually,
    "receivedNegativeTestResultEnteredManually" to receivedNegativeTestResultEnteredManually,
    "receivedVoidTestResultViaPolling" to receivedVoidTestResultViaPolling,
    "receivedPositiveTestResultViaPolling" to receivedPositiveTestResultViaPolling,
    "receivedNegativeTestResultViaPolling" to receivedNegativeTestResultViaPolling,
    "hasSelfDiagnosedBackgroundTick" to hasSelfDiagnosedBackgroundTick,
    "hasTestedPositiveBackgroundTick" to hasTestedPositiveBackgroundTick,
    "isIsolatingForSelfDiagnosedBackgroundTick" to isIsolatingForSelfDiagnosedBackgroundTick,
    "isIsolatingForTestedPositiveBackgroundTick" to isIsolatingForTestedPositiveBackgroundTick,
    "isIsolatingForHadRiskyContactBackgroundTick" to isIsolatingForHadRiskyContactBackgroundTick,
    "receivedRiskyContactNotification" to receivedRiskyContactNotification,
    "startedIsolation" to startedIsolation,
    "receivedPositiveTestResultWhenIsolatingDueToRiskyContact" to receivedPositiveTestResultWhenIsolatingDueToRiskyContact,
    "receivedActiveIpcToken" to receivedActiveIpcToken,
    "haveActiveIpcTokenBackgroundTick" to haveActiveIpcTokenBackgroundTick,
    "selectedIsolationPaymentsButton" to selectedIsolationPaymentsButton,
    "launchedIsolationPaymentsApplication" to launchedIsolationPaymentsApplication,
    "receivedPositiveLFDTestResultViaPolling" to receivedPositiveLFDTestResultViaPolling,
    "receivedNegativeLFDTestResultViaPolling" to receivedNegativeLFDTestResultViaPolling,
    "receivedVoidLFDTestResultViaPolling" to receivedVoidLFDTestResultViaPolling,
    "receivedPositiveLFDTestResultEnteredManually" to receivedPositiveLFDTestResultEnteredManually,
    "receivedNegativeLFDTestResultEnteredManually" to receivedNegativeLFDTestResultEnteredManually,
    "receivedVoidLFDTestResultEnteredManually" to receivedVoidLFDTestResultEnteredManually,
    "hasTestedLFDPositiveBackgroundTick" to hasTestedLFDPositiveBackgroundTick,
    "isIsolatingForTestedLFDPositiveBackgroundTick" to isIsolatingForTestedLFDPositiveBackgroundTick,
    "totalExposureWindowsNotConsideredRisky" to totalExposureWindowsNotConsideredRisky,
    "totalExposureWindowsConsideredRisky" to totalExposureWindowsConsideredRisky,
    "acknowledgedStartOfIsolationDueToRiskyContact" to acknowledgedStartOfIsolationDueToRiskyContact,
    "hasRiskyContactNotificationsEnabledBackgroundTick" to hasRiskyContactNotificationsEnabledBackgroundTick,
    "totalRiskyContactReminderNotifications" to totalRiskyContactReminderNotifications,
    "receivedUnconfirmedPositiveTestResult" to receivedUnconfirmedPositiveTestResult,
    "isIsolatingForUnconfirmedTestBackgroundTick" to isIsolatingForUnconfirmedTestBackgroundTick,
    "launchedTestOrdering" to launchedTestOrdering,
    "didHaveSymptomsBeforeReceivedTestResult" to didHaveSymptomsBeforeReceivedTestResult,
    "didRememberOnsetSymptomsDateBeforeReceivedTestResult" to didRememberOnsetSymptomsDateBeforeReceivedTestResult,
    "didAskForSymptomsOnPositiveTestEntry" to didAskForSymptomsOnPositiveTestEntry,
    "declaredNegativeResultFromDCT" to declaredNegativeResultFromDCT,
    "receivedPositiveSelfRapidTestResultViaPolling" to receivedPositiveSelfRapidTestResultViaPolling,
    "receivedNegativeSelfRapidTestResultViaPolling" to receivedNegativeSelfRapidTestResultViaPolling,
    "receivedVoidSelfRapidTestResultViaPolling" to receivedVoidSelfRapidTestResultViaPolling,
    "receivedPositiveSelfRapidTestResultEnteredManually" to receivedPositiveSelfRapidTestResultEnteredManually,
    "receivedNegativeSelfRapidTestResultEnteredManually" to receivedNegativeSelfRapidTestResultEnteredManually,
    "receivedVoidSelfRapidTestResultEnteredManually" to receivedVoidSelfRapidTestResultEnteredManually,
    "isIsolatingForTestedSelfRapidPositiveBackgroundTick" to isIsolatingForTestedSelfRapidPositiveBackgroundTick,
    "hasTestedSelfRapidPositiveBackgroundTick" to hasTestedSelfRapidPositiveBackgroundTick,
    "receivedRiskyVenueM1Warning" to receivedRiskyVenueM1Warning,
    "receivedRiskyVenueM2Warning" to receivedRiskyVenueM2Warning,
    "hasReceivedRiskyVenueM2WarningBackgroundTick" to hasReceivedRiskyVenueM2WarningBackgroundTick,
    "totalAlarmManagerBackgroundTasks" to totalAlarmManagerBackgroundTasks,
    "missingPacketsLast7Days" to missingPacketsLast7Days,
    "consentedToShareVenueHistory" to consentedToShareVenueHistory,
    "askedToShareVenueHistory" to askedToShareVenueHistory,
    "askedToShareExposureKeysInTheInitialFlow" to askedToShareExposureKeysInTheInitialFlow,
    "consentedToShareExposureKeysInTheInitialFlow" to consentedToShareExposureKeysInTheInitialFlow,
    "totalShareExposureKeysReminderNotifications" to totalShareExposureKeysReminderNotifications,
    "consentedToShareExposureKeysInReminderScreen" to consentedToShareExposureKeysInReminderScreen,
    "successfullySharedExposureKeys" to successfullySharedExposureKeys,
    "didSendLocalInfoNotification" to didSendLocalInfoNotification,
    "didAccessLocalInfoScreenViaNotification" to didAccessLocalInfoScreenViaNotification,
    "didAccessLocalInfoScreenViaBanner" to didAccessLocalInfoScreenViaBanner,
    "isDisplayingLocalInfoBackgroundTick" to isDisplayingLocalInfoBackgroundTick,
    "positiveLabResultAfterPositiveLFD" to positiveLabResultAfterPositiveLFD,
    "negativeLabResultAfterPositiveLFDWithinTimeLimit" to negativeLabResultAfterPositiveLFDWithinTimeLimit,
    "negativeLabResultAfterPositiveLFDOutsideTimeLimit" to negativeLabResultAfterPositiveLFDOutsideTimeLimit,
    "positiveLabResultAfterPositiveSelfRapidTest" to positiveLabResultAfterPositiveSelfRapidTest,
    "negativeLabResultAfterPositiveSelfRapidTestWithinTimeLimit" to negativeLabResultAfterPositiveSelfRapidTestWithinTimeLimit,
    "negativeLabResultAfterPositiveSelfRapidTestOutsideTimeLimit" to negativeLabResultAfterPositiveSelfRapidTestOutsideTimeLimit,
    "didAccessRiskyVenueM2Notification" to didAccessRiskyVenueM2Notification,
    "selectedTakeTestM2Journey" to selectedTakeTestM2Journey,
    "selectedTakeTestLaterM2Journey" to selectedTakeTestLaterM2Journey,
    "selectedHasSymptomsM2Journey" to selectedHasSymptomsM2Journey,
    "selectedHasNoSymptomsM2Journey" to selectedHasNoSymptomsM2Journey,
    "selectedLFDTestOrderingM2Journey" to selectedLFDTestOrderingM2Journey,
    "selectedHasLFDTestM2Journey" to selectedHasLFDTestM2Journey,
    "optedOutForContactIsolation" to optedOutForContactIsolation,
    "optedOutForContactIsolationBackgroundTick" to optedOutForContactIsolationBackgroundTick,
    "appIsUsableBackgroundTick" to appIsUsableBackgroundTick,
    "appIsContactTraceableBackgroundTick" to appIsContactTraceableBackgroundTick,
    "didAccessSelfIsolationNoteLink" to didAccessSelfIsolationNoteLink,
    "appIsUsableBluetoothOffBackgroundTick" to appIsUsableBluetoothOffBackgroundTick,
)
