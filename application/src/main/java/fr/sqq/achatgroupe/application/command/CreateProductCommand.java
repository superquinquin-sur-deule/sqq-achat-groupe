package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.Command;

import java.math.BigDecimal;

public record CreateProductCommand(
        Long venteId,
        String name,
        String description,
        BigDecimal prixHt,
        BigDecimal tauxTva,
        String supplier,
        int stock,
        String reference,
        String category,
        String brand
) implements Command<Product> {
}
