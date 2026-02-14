package fr.sqq.achatgroupe.domain.exception;

import fr.sqq.achatgroupe.domain.model.catalog.ProductId;

public class ProductNotFoundException extends DomainException {

    private final ProductId productId;

    public ProductNotFoundException(ProductId productId) {
        super("Product with id " + productId.value() + " not found");
        this.productId = productId;
    }

    public ProductId productId() {
        return productId;
    }
}
