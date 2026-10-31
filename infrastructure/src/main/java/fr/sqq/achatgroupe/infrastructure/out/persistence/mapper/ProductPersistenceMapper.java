package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductVariantEntity;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

    default Product toDomain(ProductEntity entity) {
        List<ProductVariant> variants = entity.getVariants().stream()
                .map(this::toDomain)
                .toList();

        return new Product(
                entity.getId(),
                entity.getVenteId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTauxTva(),
                entity.getSupplier(),
                entity.isActive(),
                entity.getCategory(),
                entity.getBrand(),
                entity.getImportantNote(),
                entity.isHasImage(),
                variants
        );
    }

    default ProductVariant toDomain(ProductVariantEntity entity) {
        return new ProductVariant(
                entity.getId(),
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getLabel(),
                entity.getReference(),
                Money.eur(entity.getPrixHt()),
                entity.getStock(),
                entity.getColisage(),
                entity.getPosition()
        );
    }

    default ProductEntity toEntity(Product domain) {
        var entity = new ProductEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setTauxTva(domain.tauxTva());
        entity.setSupplier(domain.supplier());
        entity.setActive(domain.active());
        entity.setCategory(domain.category());
        entity.setBrand(domain.brand());
        entity.setImportantNote(domain.importantNote());
        entity.setHasImage(domain.hasImage());

        List<ProductVariantEntity> variantEntities = new ArrayList<>();
        for (ProductVariant variant : domain.variants()) {
            variantEntities.add(toEntity(variant, entity));
        }
        entity.setVariants(variantEntities);
        return entity;
    }

    default ProductVariantEntity toEntity(ProductVariant domain, ProductEntity productEntity) {
        var entity = new ProductVariantEntity();
        entity.setId(domain.id());
        entity.setProduct(productEntity);
        applyTo(entity, domain);
        return entity;
    }

    /** Recopie les champs modifiables d'une variante sur une entité (nouvelle ou déjà managée). */
    default void applyTo(ProductVariantEntity entity, ProductVariant domain) {
        entity.setLabel(domain.label());
        entity.setReference(domain.reference());
        entity.setPrixHt(domain.prixHt().amount());
        entity.setStock(domain.stock());
        entity.setColisage(domain.colisage());
        entity.setPosition(domain.position());
    }
}
