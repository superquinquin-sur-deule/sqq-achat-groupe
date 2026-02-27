package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

    default Product toDomain(ProductEntity entity) {
        Product product = new Product(
                entity.getId(),
                entity.getVenteId(),
                entity.getName(),
                entity.getDescription(),
                Money.eur(entity.getPrixHt()),
                entity.getTauxTva(),
                entity.getSupplier(),
                entity.getStock(),
                entity.isActive(),
                entity.getReference(),
                entity.getCategory(),
                entity.getBrand(),
                entity.isHasImage()
        );
        product.assignStripeProductId(entity.getStripeProductId());
        return product;
    }

    default ProductEntity toEntity(Product domain) {
        var entity = new ProductEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setPrixHt(domain.prixHt().amount());
        entity.setTauxTva(domain.tauxTva());
        entity.setSupplier(domain.supplier());
        entity.setStock(domain.stock());
        entity.setActive(domain.active());
        entity.setReference(domain.reference());
        entity.setCategory(domain.category());
        entity.setBrand(domain.brand());
        entity.setHasImage(domain.hasImage());
        entity.setStripeProductId(domain.stripeProductId());
        return entity;
    }
}
