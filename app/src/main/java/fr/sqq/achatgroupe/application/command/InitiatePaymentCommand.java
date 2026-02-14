package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record InitiatePaymentCommand(
        Long orderId,
        String successUrl,
        String cancelUrl
) implements Command<PaymentSession> {
}
