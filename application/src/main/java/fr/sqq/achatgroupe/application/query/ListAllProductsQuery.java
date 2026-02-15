package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.Query;

public record ListAllProductsQuery(Long venteId, CursorPageRequest pageRequest) implements Query<CursorPage<Product>> {

    public static ListAllProductsQuery all(Long venteId) {
        return new ListAllProductsQuery(venteId, CursorPageRequest.first(Integer.MAX_VALUE - 1));
    }
}
