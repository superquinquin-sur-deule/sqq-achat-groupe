package fr.sqq.achatgroupe.domain.exception;

public class ProductModificationForbiddenException extends DomainException {

    public ProductModificationForbiddenException() {
        super("Impossible de modifier ce champ car la vente contient des commandes.");
    }
}
