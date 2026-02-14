package fr.sqq.achatgroupe.domain.exception;

import java.util.UUID;

public class PaymentSessionCreationException extends DomainException {

    public PaymentSessionCreationException(UUID orderId) {
        super("Impossible de créer la session de paiement pour la commande " + orderId);
    }
}
