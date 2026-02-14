package fr.sqq.achatgroupe.domain.exception;

public class PaymentAlreadySucceededException extends DomainException {

    public PaymentAlreadySucceededException(Long orderId) {
        super("Le paiement pour la commande " + orderId + " a déjà été effectué");
    }
}
