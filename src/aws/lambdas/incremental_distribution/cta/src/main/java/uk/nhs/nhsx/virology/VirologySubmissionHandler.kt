package uk.nhs.nhsx.virology

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.kms.AWSKMSClientBuilder
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import uk.nhs.nhsx.core.Clock
import uk.nhs.nhsx.core.Environment
import uk.nhs.nhsx.core.HttpResponses
import uk.nhs.nhsx.core.Json
import uk.nhs.nhsx.core.StandardSigningFactory
import uk.nhs.nhsx.core.SystemClock.CLOCK
import uk.nhs.nhsx.core.auth.ApiName.Health
import uk.nhs.nhsx.core.auth.ApiName.Mobile
import uk.nhs.nhsx.core.auth.Authenticator
import uk.nhs.nhsx.core.auth.ResponseSigner
import uk.nhs.nhsx.core.auth.StandardAuthentication.awsAuthentication
import uk.nhs.nhsx.core.aws.ssm.AwsSsmParameters
import uk.nhs.nhsx.core.events.Events
import uk.nhs.nhsx.core.events.InfoEvent
import uk.nhs.nhsx.core.events.PrintingJsonEvents
import uk.nhs.nhsx.core.events.UnprocessableJson
import uk.nhs.nhsx.core.events.VirologyCtaExchange
import uk.nhs.nhsx.core.events.VirologyOrder
import uk.nhs.nhsx.core.events.VirologyRegister
import uk.nhs.nhsx.core.events.VirologyResults
import uk.nhs.nhsx.core.handler.RoutingHandler
import uk.nhs.nhsx.core.readJsonOrNull
import uk.nhs.nhsx.core.routing.Routing.Method.POST
import uk.nhs.nhsx.core.routing.Routing.path
import uk.nhs.nhsx.core.routing.Routing.routes
import uk.nhs.nhsx.core.routing.Routing.throttlingResponse
import uk.nhs.nhsx.core.routing.authorisedBy
import uk.nhs.nhsx.core.routing.mobileAppVersionFrom
import uk.nhs.nhsx.core.routing.mobileOSFrom
import uk.nhs.nhsx.core.routing.withSignedResponses
import uk.nhs.nhsx.virology.CtaExchangeRejectionEvent.UnprocessableVirologyCtaExchange
import uk.nhs.nhsx.virology.exchange.CtaExchangeRequestV1
import uk.nhs.nhsx.virology.exchange.CtaExchangeRequestV2
import uk.nhs.nhsx.virology.exchange.CtaExchangeResult
import uk.nhs.nhsx.virology.exchange.toHttpResponse
import uk.nhs.nhsx.virology.lookup.VirologyLookupRequestV1
import uk.nhs.nhsx.virology.lookup.VirologyLookupRequestV2
import uk.nhs.nhsx.virology.lookup.VirologyLookupResult
import uk.nhs.nhsx.virology.lookup.VirologyLookupService
import uk.nhs.nhsx.virology.lookup.toHttpResponse
import uk.nhs.nhsx.virology.order.TokensGenerator
import uk.nhs.nhsx.virology.order.VirologyRequestType
import uk.nhs.nhsx.virology.order.VirologyRequestType.ORDER
import uk.nhs.nhsx.virology.order.VirologyRequestType.REGISTER
import uk.nhs.nhsx.virology.order.VirologyWebsiteConfig
import uk.nhs.nhsx.virology.persistence.VirologyPersistenceService
import uk.nhs.nhsx.virology.policy.VirologyPolicyConfig
import java.time.Duration

class VirologySubmissionHandler @JvmOverloads constructor(
    environment: Environment = Environment.fromSystem(),
    clock: Clock = CLOCK,
    delayDuration: Duration = Duration.ofSeconds(1),
    private val events: Events = PrintingJsonEvents(clock),
    mobileAuthenticator: Authenticator = awsAuthentication(Mobile, events),
    signer: ResponseSigner = StandardSigningFactory(
        clock,
        AwsSsmParameters(),
        AWSKMSClientBuilder.defaultClient()
    ).signResponseWithKeyGivenInSsm(environment, events),
    persistence: VirologyPersistenceService = VirologyPersistenceService(
        AmazonDynamoDBClientBuilder.defaultClient(),
        VirologyConfig.fromEnvironment(environment),
        events
    ),
    virology: VirologyService = VirologyService(clock, events, persistence),
    virologyLookup: VirologyLookupService = VirologyLookupService(clock, events, persistence),
    websiteConfig: VirologyWebsiteConfig = VirologyWebsiteConfig.fromEnvironment(environment),
    healthAuthenticator: Authenticator = awsAuthentication(Health, events)
) : RoutingHandler() {

    private val handler = withSignedResponses(
        events,
        environment,
        signer,
        routes(
            authorisedBy(
                healthAuthenticator,
                path(POST, "/virology-test/health") { _, _ -> HttpResponses.ok() }
            ),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/home-kit/order") { _, _ ->
                    events(VirologyOrder())
                    handleVirologyOrder(virology, websiteConfig, ORDER)
                }),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/home-kit/register") { _, _ ->
                    events(VirologyRegister())
                    handleVirologyOrder(virology, websiteConfig, REGISTER)
                }),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/results") { r, _ ->
                    events(VirologyResults())
                    Json.readJsonOrNull<VirologyLookupRequestV1>(r.body) { events(UnprocessableJson(it)) }
                        ?.let(virologyLookup::lookup)
                        ?.let(VirologyLookupResult::toHttpResponse)
                        ?: HttpResponses.unprocessableEntity()
                }),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/cta-exchange") { r, _ ->
                    events(VirologyCtaExchange())
                    throttlingResponse(delayDuration) {
                        Json.readJsonOrNull<CtaExchangeRequestV1>(r.body) { events(UnprocessableVirologyCtaExchange(it)) }
                            ?.let { virology.exchangeCtaTokenForV1(it, mobileOSFrom(r), mobileAppVersionFrom(r)) }
                            ?.let(CtaExchangeResult::toHttpResponse)
                            ?: HttpResponses.badRequest()
                    }
                }
            ),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/v2/order") { _, _ ->
                    events(VirologyOrder())
                    handleVirologyOrder(virology, websiteConfig, ORDER)
                }
            ),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/v2/results") { r, _ ->
                    events(VirologyResults())
                    Json.readJsonOrNull<VirologyLookupRequestV2>(r.body) { events(UnprocessableJson(it)) }
                        ?.let { virologyLookup.lookup(it, mobileAppVersionFrom(r)) }
                        ?.let(VirologyLookupResult::toHttpResponse)
                        ?: HttpResponses.unprocessableEntity()
                }
            ),
            authorisedBy(
                mobileAuthenticator,
                path(POST, "/virology-test/v2/cta-exchange") { r, _ ->
                    events(VirologyCtaExchange())
                    throttlingResponse(delayDuration) {
                        Json.readJsonOrNull<CtaExchangeRequestV2>(r.body) { events(UnprocessableVirologyCtaExchange(it)) }
                            ?.let { virology.exchangeCtaTokenForV2(it, mobileAppVersionFrom(r), mobileOSFrom(r)) }
                            ?.let(CtaExchangeResult::toHttpResponse)
                            ?: HttpResponses.badRequest()
                    }
                }
            )
        ))

    private fun handleVirologyOrder(
        service: VirologyService,
        websiteConfig: VirologyWebsiteConfig,
        order: VirologyRequestType
    ): APIGatewayProxyResponseEvent {
        val response = service.handleTestOrderRequest(websiteConfig, order)
        events(InfoEvent("Virology order created ctaToken: ${response.tokenParameterValue}, testResultToken: ${response.testResultPollingToken}"))
        return HttpResponses.ok(Json.toJson(response))
    }

    override fun handler() = handler

    companion object {
        private fun VirologyService(
            clock: Clock,
            events: Events,
            persistence: VirologyPersistenceService
        ) = VirologyService(
            persistence,
            TokensGenerator,
            clock,
            VirologyPolicyConfig(),
            events
        )

        private fun VirologyLookupService(
            clock: Clock,
            events: Events,
            persistence: VirologyPersistenceService
        ) = VirologyLookupService(
            persistence,
            clock,
            VirologyPolicyConfig(),
            events
        )
    }
}
