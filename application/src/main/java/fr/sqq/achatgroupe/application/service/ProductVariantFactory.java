package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.command.ProductVariantCommand;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;

import java.util.List;

public final class ProductVariantFactory {

    private ProductVariantFactory() {
    }

    /** Convertit les variantes soumises en variantes de domaine, en figeant leur ordre d'affichage. */
    public static List<ProductVariant> toDomain(Long productId, List<ProductVariantCommand> commands) {
        if (commands == null || commands.isEmpty()) {
            throw new IllegalArgumentException("Un produit doit avoir au moins une variante");
        }
        return java.util.stream.IntStream.range(0, commands.size())
                .mapToObj(i -> {
                    ProductVariantCommand c = commands.get(i);
                    return new ProductVariant(
                            c.id(),
                            productId,
                            c.label() == null ? "" : c.label(),
                            c.reference(),
                            c.prixHt(),
                            c.stock(),
                            c.colisage(),
                            i
                    );
                })
                .toList();
    }
}
