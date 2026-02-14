package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.util.UUID;

public record InitiatePaymentCommand(
        UUID orderId,
        String successUrl,
        String cancelUrl
) implements Command<PaymentSession> {
}
