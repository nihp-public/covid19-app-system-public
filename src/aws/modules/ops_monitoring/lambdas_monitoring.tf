resource "aws_cloudwatch_dashboard" "lambda_error_dashboard" {
  dashboard_name = "${var.env}-lambda-error-summary"
  dashboard_body = templatefile("${path.module}/templates/lambda_error_dashboard.tmpl", {
    analytics_ingest_submission_function           = var.analytics_ingest_submission_function,
    analytics_ingest_processing_function           = var.analytics_ingest_processing_function,
    diagnosis_keys_submission_function             = var.diagnosis_keys_submission_function,
    exposure_notification_circuit_breaker_function = var.exposure_notification_circuit_breaker_function,
    diagnosis_keys_processing_function             = var.diagnosis_keys_processing_function,
    risky_post_districts_upload_function           = var.risky_post_districts_upload_function,
    risky_venues_circuit_breaker_function          = var.risky_venues_circuit_breaker_function,
    risky_venues_upload_function                   = var.risky_venues_upload_function,
    virology_submission_function                   = var.virology_submission_function,
    virology_upload_function                       = var.virology_upload_function,
    isolation_payment_order_function               = var.isolation_payment_order_function,
    isolation_payment_verify_function              = var.isolation_payment_verify_function,
    isolation_payment_consume_function             = var.isolation_payment_consume_function
  })
}

resource "aws_cloudwatch_dashboard" "lambda_warning_dashboard" {
  dashboard_name = "${var.env}-lambda-warning-summary"
  dashboard_body = templatefile("${path.module}/templates/lambda_warning_dashboard.tmpl", {
    analytics_ingest_submission_function           = var.analytics_ingest_submission_function,
    analytics_ingest_processing_function           = var.analytics_ingest_processing_function,
    diagnosis_keys_submission_function             = var.diagnosis_keys_submission_function,
    exposure_notification_circuit_breaker_function = var.exposure_notification_circuit_breaker_function,
    diagnosis_keys_processing_function             = var.diagnosis_keys_processing_function,
    risky_post_districts_upload_function           = var.risky_post_districts_upload_function,
    risky_venues_circuit_breaker_function          = var.risky_venues_circuit_breaker_function,
    risky_venues_upload_function                   = var.risky_venues_upload_function,
    virology_submission_function                   = var.virology_submission_function,
    virology_upload_function                       = var.virology_upload_function,
    isolation_payment_order_function               = var.isolation_payment_order_function,
    isolation_payment_verify_function              = var.isolation_payment_verify_function,
    isolation_payment_consume_function             = var.isolation_payment_consume_function
  })
}

resource "aws_cloudwatch_dashboard" "diagnosis_keys_dashboard" {
  dashboard_name = "${var.env}-diagnosis-keys-dashboard"
  dashboard_body = templatefile("${path.module}/templates/diagnosis_keys_dashboard.tmpl", {
    diagnosis_keys_processing_function = var.diagnosis_keys_processing_function,
    diagnosis_keys_submission_function = var.diagnosis_keys_submission_function
  })
}

resource "aws_cloudwatch_dashboard" "sip_dashboard" {
  dashboard_name = "${var.env}-sip-dashboard"
  dashboard_body = templatefile("${path.module}/templates/sip_dashboard.tmpl", {
    env = var.env
  })
}

resource "aws_cloudwatch_dashboard" "virology_dashboard" {
  dashboard_name = "${var.env}-virology-dashboard"
  dashboard_body = templatefile("${path.module}/templates/virology_dashboard.tmpl", {
    virology_submission_function = var.virology_submission_function,
    virology_upload_function     = var.virology_upload_function
    submission_api_gateway_id    = var.virology_submission_api_gateway_id
    upload_api_gateway_id        = var.virology_upload_api_gateway_id
    env                          = var.env
  })
}

resource "aws_cloudwatch_dashboard" "federation_keys_dashboard" {
  dashboard_name = "${var.env}-federation-keys-dashboard"
  dashboard_body = templatefile("${path.module}/templates/federation_keys_dashboard.tmpl", {
    federation_keys_processing_upload_function   = var.federation_keys_processing_upload_function
    federation_keys_processing_download_function = var.federation_keys_processing_download_function
  })
}

resource "aws_cloudwatch_dashboard" "duplicate_cta_token_dashboard" {
  dashboard_name = "${var.env}-duplicate-cta-token-dashboard"
  dashboard_body = templatefile("${path.module}/templates/duplicate_ctatoken_dashboard.tmpl", {
    virology_upload_function = var.virology_upload_function
  })
}

resource "aws_cloudwatch_dashboard" "api_gateway_dashboard" {
  dashboard_name = "${var.env}-api-gateway-dashboard"
  dashboard_body = templatefile("${path.module}/templates/api_gateway_dashboard.tmpl", {
    analytics_events_submission_gateway_id           = var.analytics_events_submission_gateway_id
    analytics_submission_fast_ingest_gateway_id      = var.analytics_submission_fast_ingest_gateway_id
    crash_reports_submission_gateway_id              = var.crash_reports_submission_gateway_id
    diagnosis_keys_submission_gateway_id             = var.diagnosis_keys_submission_gateway_id
    exposure_notification_circuit_breaker_gateway_id = var.exposure_notification_circuit_breaker_gateway_id
    empty_submission_gateway_id                      = var.empty_submission_gateway_id
    isolation_payment_submission_gateway_id          = var.isolation_payment_submission_gateway_id
    risky_post_districts_upload_gateway_id           = var.risky_post_districts_upload_gateway_id
    risky_venues_upload_gateway_id                   = var.risky_venues_upload_gateway_id
    risky_venues_circuit_breaker_gateway_id          = var.risky_venues_circuit_breaker_gateway_id
    virology_submission_api_gateway_id               = var.virology_submission_api_gateway_id
    virology_upload_api_gateway_id                   = var.virology_upload_api_gateway_id
  })
}

resource "aws_cloudwatch_dashboard" "load_profile_dashboard" {
  dashboard_name = "${var.env}-load-profile-dashboard"
  dashboard_body = templatefile("${path.module}/templates/load_profile_dashboard.tmpl", {
    analytics_events_submission_gateway_id         = var.analytics_events_submission_gateway_id,
    analytics_submission_fast_ingest_gateway_id    = var.analytics_submission_fast_ingest_gateway_id,
    diagnosis_keys_submission_function             = var.diagnosis_keys_submission_function,
    empty_submission_gateway_id                    = var.empty_submission_gateway_id,
    exposure_notification_circuit_breaker_function = var.exposure_notification_circuit_breaker_function,
    virology_submission_function                   = var.virology_submission_function
  })
}

resource "aws_cloudwatch_dashboard" "virology_api_response_dashboard" {
  dashboard_name = "${var.env}-virology-api-response-dashboard"
  dashboard_body = templatefile("${path.module}/templates/virology_api_response_dashboard.tmpl", {
    virology_submission_function       = var.virology_submission_function,
    virology_upload_function           = var.virology_upload_function
    virology_submission_api_gateway_id = var.virology_submission_api_gateway_id
    virology_upload_api_gateway_id     = var.virology_upload_api_gateway_id
  })
}

resource "aws_cloudwatch_dashboard" "high_traffic_api_dashboard" {
  dashboard_name = "${var.env}-high-traffic-api-dashboard"
  dashboard_body = templatefile("${path.module}/templates/high_traffic_api_dashboard.tmpl", {
    cloudfront_submission_id             = var.cloudfront_submission_id
    analytics_events_function            = var.analytics_events_function
    analytics_ingest_submission_function = var.analytics_ingest_submission_function
    empty_submission_function            = var.empty_submission_function
    analytics_events_api_gateway_id      = var.analytics_events_submission_gateway_id
    analytics_ingest_api_gateway_id      = var.analytics_submission_fast_ingest_gateway_id
    empty_submission_api_gateway_id      = var.empty_submission_gateway_id
  })
}
