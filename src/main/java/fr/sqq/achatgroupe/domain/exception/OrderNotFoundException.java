package fr.sqq.achatgroupe.domain.exception;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(Long orderId) {
        super("Commande introuvable : " + orderId);
    }
}
