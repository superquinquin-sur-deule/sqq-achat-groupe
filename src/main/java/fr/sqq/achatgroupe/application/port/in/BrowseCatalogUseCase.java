package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;

import java.util.List;

public interface BrowseCatalogUseCase {

    List<Product> listActiveProducts(Long venteId);

    Product getProduct(ProductId id);
}
