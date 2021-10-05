module "cloudwatch_analytics" {
  source                     = "./modules/ops_monitoring"
  env                        = terraform.workspace
  cloudfront_distribution_id = module.distribution_apis.distribution_id
  cloudfront_submission_id   = module.submission_apis.distribution_id
  cloudfront_upload_id       = module.upload_apis.distribution_id
  monitored_buckets = [
    module.diagnosis_keys_distribution_store.bucket_id,
    module.diagnosis_keys_submission.store_id,
  ]
  request_triggered = [
    module.risky_venues_upload.lambda_function_name,
    module.risky_post_districts_upload.lambda_function_name,
    module.virology_submission.lambda_function_name,
    module.diagnosis_keys_submission.function,
    module.risky_venues_circuit_breaker.function,
    module.exposure_notification_circuit_breaker.function,
    module.analytics_submission_fast_ingest.submission_lambda_function_name,
    module.virology_upload.lambda_function_name
  ]
  gateways = [
    module.diagnosis_keys_submission.gateway_id,
    module.risky_post_districts_upload.gateway_id,
    module.virology_upload.gateway_id,
    module.analytics_submission_fast_ingest.gateway_id,
    module.risky_venues_upload.gateway_id,
    module.virology_submission.gateway_id,
    module.risky_venues_circuit_breaker.gateway_id,
    module.exposure_notification_circuit_breaker.gateway_id
  ]

  analytics_events_function                        = module.analytics_events_submission.function
  analytics_ingest_submission_function             = module.analytics_submission_fast_ingest.submission_lambda_function_name
  analytics_ingest_processing_function             = module.analytics_submission_fast_ingest.processing_lambda_function_name
  diagnosis_keys_submission_function               = module.diagnosis_keys_submission.function
  empty_submission_function                        = module.empty_submission.function
  federation_keys_processing_upload_function       = module.federation_keys_processing.upload_lambda_function
  federation_keys_processing_download_function     = module.federation_keys_processing.download_lambda_function
  exposure_notification_circuit_breaker_function   = module.exposure_notification_circuit_breaker.function
  diagnosis_keys_processing_function               = module.diagnosis_keys_processing.function
  risky_post_districts_upload_function             = module.risky_post_districts_upload.lambda_function_name
  risky_venues_circuit_breaker_function            = module.risky_venues_circuit_breaker.function
  risky_venues_upload_function                     = module.risky_venues_upload.lambda_function_name
  virology_submission_function                     = module.virology_submission.lambda_function_name
  virology_upload_function                         = module.virology_upload.lambda_function_name
  isolation_payment_order_function                 = module.isolation_payment_submission.order_lambda_function_name
  isolation_payment_verify_function                = module.isolation_payment_submission.verify_lambda_function_name
  isolation_payment_consume_function               = module.isolation_payment_submission.consume_lambda_function_name
  analytics_events_submission_gateway_id           = module.analytics_events_submission.gateway_id
  analytics_submission_fast_ingest_gateway_id      = module.analytics_submission_fast_ingest.gateway_id
  crash_reports_submission_gateway_id              = module.crash_reports_submission.gateway_id
  diagnosis_keys_submission_gateway_id             = module.diagnosis_keys_submission.gateway_id
  empty_submission_gateway_id                      = module.empty_submission.gateway_id
  exposure_notification_circuit_breaker_gateway_id = module.exposure_notification_circuit_breaker.gateway_id
  isolation_payment_submission_gateway_id          = module.isolation_payment_submission.endpoint
  risky_post_districts_upload_gateway_id           = module.risky_post_districts_upload.gateway_id
  risky_venues_upload_gateway_id                   = module.risky_venues_upload.gateway_id
  risky_venues_circuit_breaker_gateway_id          = module.risky_venues_circuit_breaker.gateway_id
  virology_submission_api_gateway_id               = module.virology_submission.gateway_id
  virology_upload_api_gateway_id                   = module.virology_upload.gateway_id

  shield_ddos_alarms_sns_arn = var.shield_ddos_alarms_sns_arn
  shield_protected_arns = [
    module.distribution_apis.distribution_arn,
    module.submission_apis.distribution_arn,
    module.upload_apis.distribution_arn
  ]
  tags = var.tags
}
