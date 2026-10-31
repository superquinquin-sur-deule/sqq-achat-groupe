package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductVariantResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ProductRestMapper {

    default ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        List<ProductVariantResponse> variants = product.variants().stream()
                .map(variant -> toResponse(product, variant))
                .toList();

        return new ProductResponse(
                product.id(),
                product.name(),
                product.description(),
                product.tauxTva(),
                product.supplier(),
                product.category(),
                product.brand(),
                product.importantNote(),
                product.hasImage() ? "/api/product-images/" + product.id() : null,
                variants
        );
    }

    default ProductVariantResponse toResponse(Product product, ProductVariant variant) {
        return new ProductVariantResponse(
                variant.id(),
                variant.label(),
                variant.prixHt().amount(),
                product.prixTtc(variant).amount(),
                variant.stock()
        );
    }
}
