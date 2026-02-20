package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

    Product toDomain(ProductEntity entity);

    @Mapping(target = "id", expression = "java(domain.id())")
    @Mapping(target = "venteId", expression = "java(domain.venteId())")
    @Mapping(target = "name", expression = "java(domain.name())")
    @Mapping(target = "description", expression = "java(domain.description())")
    @Mapping(target = "price", expression = "java(domain.price())")
    @Mapping(target = "supplier", expression = "java(domain.supplier())")
    @Mapping(target = "stock", expression = "java(domain.stock())")
    @Mapping(target = "active", expression = "java(domain.active())")
    @Mapping(target = "reference", expression = "java(domain.reference())")
    @Mapping(target = "category", expression = "java(domain.category())")
    @Mapping(target = "brand", expression = "java(domain.brand())")
    ProductEntity toEntity(Product domain);
}
