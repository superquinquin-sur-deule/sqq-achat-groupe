package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAllActiveByVenteId(Long venteId);

    CursorPage<Product> findAllActiveByVenteId(Long venteId, CursorPageRequest pageRequest);

    Optional<Product> findById(ProductId id);

    Optional<Product> findByIdAndVenteId(ProductId id, Long venteId);

    List<Product> findAllByVenteId(Long venteId);

    CursorPage<Product> findAllByVenteId(Long venteId, CursorPageRequest pageRequest);

    void save(Product product);

    Product saveNew(Product product);

    void deleteById(ProductId id);
}
