package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

    Product toDomain(ProductEntity entity);

    @AfterMapping
    default void mapStripeProductId(@MappingTarget Product product, ProductEntity entity) {
        product.assignStripeProductId(entity.getStripeProductId());
    }

    @Mapping(target = "id", expression = "java(domain.id())")
    @Mapping(target = "venteId", expression = "java(domain.venteId())")
    @Mapping(target = "name", expression = "java(domain.name())")
    @Mapping(target = "description", expression = "java(domain.description())")
    @Mapping(target = "prixHt", expression = "java(domain.prixHt())")
    @Mapping(target = "tauxTva", expression = "java(domain.tauxTva())")
    @Mapping(target = "supplier", expression = "java(domain.supplier())")
    @Mapping(target = "stock", expression = "java(domain.stock())")
    @Mapping(target = "active", expression = "java(domain.active())")
    @Mapping(target = "reference", expression = "java(domain.reference())")
    @Mapping(target = "category", expression = "java(domain.category())")
    @Mapping(target = "brand", expression = "java(domain.brand())")
    @Mapping(target = "hasImage", expression = "java(domain.hasImage())")
    @Mapping(target = "stripeProductId", expression = "java(domain.stripeProductId())")
    ProductEntity toEntity(Product domain);
}
