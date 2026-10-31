package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.mediator.Command;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductCommand(
        Long venteId,
        ProductId id,
        String name,
        String description,
        BigDecimal tauxTva,
        String supplier,
        boolean active,
        String category,
        String brand,
        String importantNote,
        List<ProductVariantCommand> variants
) implements Command<Product> {
}
