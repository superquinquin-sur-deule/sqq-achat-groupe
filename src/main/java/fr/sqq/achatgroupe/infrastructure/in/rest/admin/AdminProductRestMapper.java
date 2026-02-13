package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase.CreateProductCommand;
import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase.UpdateProductCommand;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.AdminProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateProductRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateProductRequest;

public class AdminProductRestMapper {

    private AdminProductRestMapper() {
    }

    public static AdminProductResponse toResponse(Product product) {
        return new AdminProductResponse(
                product.id(),
                product.venteId(),
                product.name(),
                product.description(),
                product.price(),
                product.supplier(),
                product.stock(),
                product.active()
        );
    }

    public static CreateProductCommand toCreateCommand(CreateProductRequest request) {
        return new CreateProductCommand(
                request.venteId(),
                request.name(),
                request.description(),
                request.price(),
                request.supplier(),
                request.stock()
        );
    }

    public static UpdateProductCommand toUpdateCommand(Long id, UpdateProductRequest request) {
        return new UpdateProductCommand(
                new ProductId(id),
                request.name(),
                request.description(),
                request.price(),
                request.supplier(),
                request.stock(),
                request.active()
        );
    }
}
