package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.command.CreateProductCommand;
import fr.sqq.achatgroupe.application.command.ProductVariantCommand;
import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminProductVariantResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateProductRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductVariantRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateProductRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AdminProductRestMapper {

    default AdminProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        List<AdminProductVariantResponse> variants = product.variants().stream()
                .map(variant -> toResponse(product, variant))
                .toList();

        return new AdminProductResponse(
                product.id(),
                product.venteId(),
                product.name(),
                product.description(),
                product.tauxTva(),
                product.supplier(),
                product.active(),
                product.category(),
                product.brand(),
                product.importantNote(),
                product.hasImage() ? "/api/product-images/" + product.id() : null,
                variants
        );
    }

    default AdminProductVariantResponse toResponse(Product product, ProductVariant variant) {
        return new AdminProductVariantResponse(
                variant.id(),
                variant.label(),
                variant.reference(),
                variant.prixHt().amount(),
                product.prixTtc(variant).amount(),
                variant.stock(),
                variant.colisage()
        );
    }

    default CreateProductCommand toCreateCommand(CreateProductRequest request, Long venteId) {
        return new CreateProductCommand(
                venteId,
                request.name(),
                request.description(),
                request.tauxTva(),
                request.supplier(),
                request.category(),
                request.brand(),
                request.importantNote(),
                toVariantCommands(request.variants())
        );
    }

    default UpdateProductCommand toUpdateCommand(Long id, UpdateProductRequest request, Long venteId) {
        return new UpdateProductCommand(
                venteId,
                new ProductId(id),
                request.name(),
                request.description(),
                request.tauxTva(),
                request.supplier(),
                request.active(),
                request.category(),
                request.brand(),
                request.importantNote(),
                toVariantCommands(request.variants())
        );
    }

    default List<ProductVariantCommand> toVariantCommands(List<ProductVariantRequest> requests) {
        if (requests == null) {
            return List.of();
        }
        return requests.stream()
                .map(r -> new ProductVariantCommand(
                        r.id(),
                        r.label() == null ? "" : r.label(),
                        r.reference(),
                        Money.eur(r.prixHt()),
                        r.stock(),
                        r.colisage()))
                .toList();
    }
}
