package fr.sqq.achatgroupe.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID orderId) {
        super("Commande introuvable : " + orderId);
    }
}
