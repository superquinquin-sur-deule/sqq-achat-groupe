package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.mediator.Query;

public record GetProductQuery(ProductId id) implements Query<Product> {
}
