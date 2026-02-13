package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ProductResponse;

public class ProductRestMapper {

    private ProductRestMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.supplier(),
                product.stock()
        );
    }
}
