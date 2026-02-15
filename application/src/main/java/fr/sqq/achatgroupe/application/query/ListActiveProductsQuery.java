package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.Query;

public record ListActiveProductsQuery(Long venteId, CursorPageRequest pageRequest) implements Query<CursorPage<Product>> {
}
