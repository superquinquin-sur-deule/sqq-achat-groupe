package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;

public class ProductPersistenceMapper {

    private ProductPersistenceMapper() {
    }

    public static Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getVenteId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getSupplier(),
                entity.getStock(),
                entity.isActive()
        );
    }

    public static ProductEntity toEntity(Product domain) {
        var entity = new ProductEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setPrice(domain.price());
        entity.setSupplier(domain.supplier());
        entity.setStock(domain.stock());
        entity.setActive(domain.active());
        return entity;
    }
}
