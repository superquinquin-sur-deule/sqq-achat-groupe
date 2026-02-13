package fr.sqq.achatgroupe.domain.exception;

public class OrderAlreadyPaidException extends DomainException {

    public OrderAlreadyPaidException(Long orderId) {
        super("La commande " + orderId + " a déjà été payée");
    }
}
