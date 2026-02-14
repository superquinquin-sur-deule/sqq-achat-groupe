package fr.sqq.achatgroupe.domain.exception;

import java.util.UUID;

public class OrderAlreadyPaidException extends DomainException {

    public OrderAlreadyPaidException(UUID orderId) {
        super("La commande " + orderId + " a déjà été payée");
    }
}
