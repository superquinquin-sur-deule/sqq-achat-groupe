package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductRestMapper {

    @Mapping(target = "id", expression = "java(product.id())")
    @Mapping(target = "name", expression = "java(product.name())")
    @Mapping(target = "description", expression = "java(product.description())")
    @Mapping(target = "price", expression = "java(product.price())")
    @Mapping(target = "supplier", expression = "java(product.supplier())")
    @Mapping(target = "stock", expression = "java(product.stock())")
    @Mapping(target = "category", expression = "java(product.category())")
    @Mapping(target = "brand", expression = "java(product.brand())")
    @Mapping(target = "imageUrl", expression = "java(product.hasImage() ? \"/api/product-images/\" + product.id() : null)")
    ProductResponse toResponse(Product product);
}
