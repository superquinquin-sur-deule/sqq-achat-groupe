package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.mediator.Command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Long venteId,
        ProductId id,
        String name,
        String description,
        BigDecimal price,
        String supplier,
        int stock,
        boolean active,
        String reference,
        String category,
        String brand
) implements Command<Product> {
}
