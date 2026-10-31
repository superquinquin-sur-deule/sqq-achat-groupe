package fr.sqq.achatgroupe.domain.exception;

import fr.sqq.achatgroupe.domain.model.catalog.VariantId;

public class VariantNotFoundException extends DomainException {

    private final VariantId variantId;

    public VariantNotFoundException(VariantId variantId) {
        super("Variant with id " + variantId.value() + " not found");
        this.variantId = variantId;
    }

    public VariantNotFoundException(Long variantId) {
        super("Variant with id " + variantId + " not found");
        this.variantId = null;
    }

    public VariantId variantId() {
        return variantId;
    }
}
