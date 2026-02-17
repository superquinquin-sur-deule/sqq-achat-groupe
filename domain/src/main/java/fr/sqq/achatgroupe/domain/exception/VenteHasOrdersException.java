package fr.sqq.achatgroupe.domain.exception;

public class VenteHasOrdersException extends DomainException {

    public VenteHasOrdersException() {
        super("Impossible de supprimer la vente car elle contient des commandes.");
    }
}
