package fr.sqq.achatgroupe.domain.exception;

public class ProductModificationForbiddenException extends DomainException {

    public ProductModificationForbiddenException() {
        super("Impossible de modifier/supprimer ce produit car la vente contient des commandes. Seule la désactivation est autorisée.");
    }
}
