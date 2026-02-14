package fr.sqq.achatgroupe.domain.exception;

public class PaymentSessionCreationException extends DomainException {

    public PaymentSessionCreationException(Long orderId) {
        super("Impossible de créer la session de paiement pour la commande " + orderId);
    }
}
