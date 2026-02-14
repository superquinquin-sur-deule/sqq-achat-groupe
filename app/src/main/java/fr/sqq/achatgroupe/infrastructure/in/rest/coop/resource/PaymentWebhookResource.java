package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.command.HandlePaymentResultCommand;
import fr.sqq.mediator.Mediator;
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

    private final Mediator mediator;

    public PaymentWebhookResource(Mediator mediator) {
        this.mediator = mediator;
    }

    @POST
    @Operation(hidden = true)
    public Response handleStripeWebhook(String payload, @HeaderParam("Stripe-Signature") String signature) {
        mediator.send(new HandlePaymentResultCommand(payload, signature));
        return Response.ok().build();
    }
}
