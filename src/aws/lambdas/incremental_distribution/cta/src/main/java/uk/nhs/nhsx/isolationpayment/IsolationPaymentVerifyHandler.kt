package uk.nhs.nhsx.isolationpayment

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import uk.nhs.nhsx.core.Clock
import uk.nhs.nhsx.core.Environment
import uk.nhs.nhsx.core.Handler
import uk.nhs.nhsx.core.SystemClock
import uk.nhs.nhsx.core.events.Events
import uk.nhs.nhsx.core.events.PrintingJsonEvents
import uk.nhs.nhsx.core.handler.DirectHandler
import uk.nhs.nhsx.isolationpayment.IsolationPaymentSettings.AUDIT_LOG_PREFIX
import uk.nhs.nhsx.isolationpayment.IsolationPaymentSettings.ISOLATION_TOKEN_TABLE
import uk.nhs.nhsx.isolationpayment.model.IsolationRequest
import uk.nhs.nhsx.isolationpayment.model.IsolationResponse

@Suppress("unused")
class IsolationPaymentVerifyHandler(private val service: IsolationPaymentGatewayService,
                                    events: Events) : DirectHandler<IsolationRequest, IsolationResponse>(events, IsolationRequest::class) {
    @JvmOverloads
    constructor(clock: Clock = SystemClock.CLOCK,
                environment: Environment = Environment.fromSystem(),
                events: Events = PrintingJsonEvents(clock)) : this(
        IsolationPaymentGatewayService(clock,
            IsolationPaymentPersistence(
                AmazonDynamoDBClientBuilder.defaultClient(),
                environment.access.required(ISOLATION_TOKEN_TABLE)
            ),
            environment.access.required<String>(AUDIT_LOG_PREFIX), events), events)

    override fun handler() = Handler<IsolationRequest, IsolationResponse> { input, _ ->
        service.verifyIsolationToken(input.ipcToken)
    }
}
