package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAllActiveByVenteId(Long venteId);

    Optional<Product> findById(ProductId id);

    List<Product> findAllByVenteId(Long venteId);

    void save(Product product);

    Product saveNew(Product product);

    void deleteById(ProductId id);
}
