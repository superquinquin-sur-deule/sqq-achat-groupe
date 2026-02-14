package fr.sqq.achatgroupe.domain.exception;

import java.util.UUID;

public class PaymentAlreadySucceededException extends DomainException {

    public PaymentAlreadySucceededException(UUID orderId) {
        super("Le paiement pour la commande " + orderId + " a déjà été effectué");
    }
}
