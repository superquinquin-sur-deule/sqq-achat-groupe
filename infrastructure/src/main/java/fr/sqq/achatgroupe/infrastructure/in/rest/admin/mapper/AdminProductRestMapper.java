package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.command.CreateProductCommand;
import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateProductRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface AdminProductRestMapper {

    @Mapping(target = "id", expression = "java(product.id())")
    @Mapping(target = "venteId", expression = "java(product.venteId())")
    @Mapping(target = "name", expression = "java(product.name())")
    @Mapping(target = "description", expression = "java(product.description())")
    @Mapping(target = "price", expression = "java(product.price())")
    @Mapping(target = "supplier", expression = "java(product.supplier())")
    @Mapping(target = "stock", expression = "java(product.stock())")
    @Mapping(target = "active", expression = "java(product.active())")
    @Mapping(target = "reference", expression = "java(product.reference())")
    @Mapping(target = "category", expression = "java(product.category())")
    @Mapping(target = "brand", expression = "java(product.brand())")
    AdminProductResponse toResponse(Product product);

    default CreateProductCommand toCreateCommand(CreateProductRequest request, Long venteId) {
        return new CreateProductCommand(
                venteId,
                request.name(),
                request.description(),
                request.price(),
                request.supplier(),
                request.stock(),
                request.reference(),
                request.category(),
                request.brand()
        );
    }

    default UpdateProductCommand toUpdateCommand(Long id, UpdateProductRequest request, Long venteId) {
        return new UpdateProductCommand(
                venteId,
                new ProductId(id),
                request.name(),
                request.description(),
                request.price(),
                request.supplier(),
                request.stock(),
                request.active(),
                request.reference(),
                request.category(),
                request.brand()
        );
    }
}
