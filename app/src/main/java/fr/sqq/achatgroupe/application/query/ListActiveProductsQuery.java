package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.Query;

import java.util.List;

public record ListActiveProductsQuery(Long venteId) implements Query<List<Product>> {
}
