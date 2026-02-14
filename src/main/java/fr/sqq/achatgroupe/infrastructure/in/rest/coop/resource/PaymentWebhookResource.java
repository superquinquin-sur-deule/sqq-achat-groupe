package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.port.in.HandlePaymentResultUseCase;
import fr.sqq.achatgroupe.application.port.in.HandlePaymentResultUseCase.HandlePaymentResultCommand;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/webhooks/stripe")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "webhooks")
public class PaymentWebhookResource {

    private final HandlePaymentResultUseCase handlePaymentResultUseCase;

    public PaymentWebhookResource(HandlePaymentResultUseCase handlePaymentResultUseCase) {
        this.handlePaymentResultUseCase = handlePaymentResultUseCase;
    }

    @POST
    @Operation(hidden = true)
    public Response handleStripeWebhook(String payload, @HeaderParam("Stripe-Signature") String signature) {
        var command = new HandlePaymentResultCommand(payload, signature);
        handlePaymentResultUseCase.execute(command);
        return Response.ok().build();
    }
}
