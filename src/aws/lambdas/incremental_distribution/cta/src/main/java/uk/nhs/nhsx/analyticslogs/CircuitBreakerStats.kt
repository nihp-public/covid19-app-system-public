package uk.nhs.nhsx.analyticslogs

import uk.nhs.nhsx.core.Environment
import uk.nhs.nhsx.core.SystemClock
import uk.nhs.nhsx.core.SystemClock.CLOCK
import uk.nhs.nhsx.core.events.PrintingJsonEvents

data class CircuitBreakerStats(
    val startOfHour: String,
    val exposureNotificationCBCount: Int,
    val iosExposureNotificationCBCount: Int,
    val androidExposureNotificationCBCount: Int,
    val uniqueRequestIds: Int,
    val appVersion: String
)

class CircuitBreakerStatsConverter : Converter<CircuitBreakerStats>() {
    override fun convert(map: Map<String, String>) = CircuitBreakerStats(
        startOfHour = map.getValue("start_of_hour"),
        exposureNotificationCBCount = map.getOrDefault("exposure_notification_cb_count", "0").toInt(),
        iosExposureNotificationCBCount = map.getOrDefault("iOS_exposure_notification_cb_count", "0").toInt(),
        androidExposureNotificationCBCount = map.getOrDefault("android_exposure_notification_cb_count", "0").toInt(),
        uniqueRequestIds = map.getOrDefault("unique_request_ids", "0").toInt(),
        appVersion = map.getOrDefault("app_version", "UNKNOWN")
    )
}

class CircuitBreakerAnalyticsHandler : LogInsightsAnalyticsHandler(
    service = logAnalyticsService(
        environment = Environment.fromSystem(),
        clock = CLOCK,
        events = PrintingJsonEvents(CLOCK),
        queryString = circuitBreakerQueryString,
        converter = CircuitBreakerStatsConverter()
    ),
    events = PrintingJsonEvents(CLOCK)
)

private const val circuitBreakerQueryString = """fields @timestamp, @message
| filter @message like /^\{/
| filter @message like /Received http request.*exposure-notification\/request,requestId=.*,apiKeyName=mobile/
| parse @message /Received http request.*exposure-notification\/request,requestId=(?<requestId>.*),apiKeyName=mobile/
| parse @message /(?<iOS>=[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12},)/
| parse @message /(?<android>=[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12},)/
| stats coalesce(count(android), 0) + coalesce(count(iOS), 0) as exposure_notification_cb_count, floor(coalesce(count(iOS), 0)) as iOS_exposure_notification_cb_count,  floor(coalesce(count(android), 0)) as android_exposure_notification_cb_count, count_distinct(requestId) as unique_request_ids by bin(1h) as start_of_hour, event.userAgent.appVersion.semVer as app_version"""
