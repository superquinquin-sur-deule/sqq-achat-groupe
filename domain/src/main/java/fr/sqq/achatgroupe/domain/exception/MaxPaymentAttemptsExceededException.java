package fr.sqq.achatgroupe.domain.exception;

import java.util.UUID;

public class MaxPaymentAttemptsExceededException extends DomainException {

    public MaxPaymentAttemptsExceededException(UUID orderId, int maxAttempts) {
        super("Le nombre maximum de tentatives de paiement (" + maxAttempts + ") a été atteint pour la commande " + orderId);
    }
}
