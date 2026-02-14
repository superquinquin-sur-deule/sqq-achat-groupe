package fr.sqq.achatgroupe.infrastructure.in.rest.common.middleware;

import fr.sqq.achatgroupe.domain.exception.DomainException;
import fr.sqq.achatgroupe.domain.exception.InsufficientStockException;
import fr.sqq.achatgroupe.domain.exception.OrderAlreadyPaidException;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.exception.PaymentAlreadySucceededException;
import fr.sqq.achatgroupe.domain.exception.PaymentSessionCreationException;
import fr.sqq.achatgroupe.domain.exception.PaymentWebhookException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotFullException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotHasReservationsException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProblemDetailResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @Override
    public Response toResponse(DomainException e) {
        if (e instanceof ProductNotFoundException || e instanceof OrderNotFoundException) {
            return buildResponse("about:blank", "Not Found", 404, e.getMessage());
        }
        if (e instanceof TimeSlotNotFoundException) {
            return buildResponse("about:blank", "Not Found", 404, e.getMessage());
        }
        if (e instanceof VenteNotFoundException) {
            return buildResponse("about:blank", "Not Found", 404, e.getMessage());
        }
        if (e instanceof InsufficientStockException) {
            return buildResponse("https://api.sqq.fr/errors/insufficient-stock", "Stock insuffisant", 409, e.getMessage());
        }
        if (e instanceof TimeSlotFullException) {
            return buildResponse("https://api.sqq.fr/errors/timeslot-full", "Créneau complet", 409, e.getMessage());
        }
        if (e instanceof TimeSlotHasReservationsException) {
            return buildResponse("https://api.sqq.fr/errors/timeslot-has-reservations", "Créneau avec réservations", 409, e.getMessage());
        }
        if (e instanceof OrderAlreadyPaidException) {
            return buildResponse("https://api.sqq.fr/errors/order-already-paid", "Commande déjà payée", 409, e.getMessage());
        }
        if (e instanceof PaymentAlreadySucceededException) {
            return buildResponse("https://api.sqq.fr/errors/payment-already-succeeded", "Paiement déjà effectué", 409, e.getMessage());
        }
        if (e instanceof PaymentSessionCreationException) {
            return buildResponse("https://api.sqq.fr/errors/payment-session-creation", "Erreur de paiement", 502, e.getMessage());
        }
        if (e instanceof PaymentWebhookException) {
            return buildResponse("about:blank", "Webhook Error", 400, e.getMessage());
        }
        return buildResponse("about:blank", "Domain Error", 400, e.getMessage());
    }

    private Response buildResponse(String type, String title, int status, String detail) {
        var problem = new ProblemDetailResponse(type, title, status, detail);
        return Response.status(status)
                .type(PROBLEM_JSON)
                .entity(problem)
                .build();
    }
}
