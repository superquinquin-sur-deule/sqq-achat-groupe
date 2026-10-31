package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.Command;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        Long venteId,
        String name,
        String description,
        BigDecimal tauxTva,
        String supplier,
        String category,
        String brand,
        String importantNote,
        List<ProductVariantCommand> variants
) implements Command<Product> {
}
