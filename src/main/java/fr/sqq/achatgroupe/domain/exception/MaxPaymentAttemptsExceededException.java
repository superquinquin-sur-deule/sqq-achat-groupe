package fr.sqq.achatgroupe.domain.exception;

public class MaxPaymentAttemptsExceededException extends DomainException {

    public MaxPaymentAttemptsExceededException(Long orderId, int maxAttempts) {
        super("Le nombre maximum de tentatives de paiement (" + maxAttempts + ") a été atteint pour la commande " + orderId);
    }
}
