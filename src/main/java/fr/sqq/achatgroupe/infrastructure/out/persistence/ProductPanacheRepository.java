package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductPanacheRepository implements ProductRepository, PanacheRepositoryBase<ProductEntity, Long> {

    @Override
    public List<Product> findAllActiveByVenteId(Long venteId) {
        return list("active = true and venteId = ?1", venteId).stream()
                .map(ProductPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return find("id", id.value()).firstResultOptional()
                .map(ProductPersistenceMapper::toDomain);
    }

    @Override
    public List<Product> findAllByVenteId(Long venteId) {
        return list("venteId = ?1", venteId).stream()
                .map(ProductPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Product product) {
        ProductEntity entity = findById(product.id());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setPrice(product.price());
        entity.setSupplier(product.supplier());
        entity.setStock(product.stock());
        entity.setActive(product.active());
    }

    @Override
    public Product saveNew(Product product) {
        ProductEntity entity = ProductPersistenceMapper.toEntity(product);
        persist(entity);
        return ProductPersistenceMapper.toDomain(entity);
    }

    @Override
    public void deleteById(ProductId id) {
        delete("id", id.value());
    }
}
