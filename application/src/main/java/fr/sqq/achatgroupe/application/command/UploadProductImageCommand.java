package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.mediator.Command;

public record UploadProductImageCommand(
        Long venteId,
        ProductId productId,
        byte[] imageData,
        String contentType
) implements Command<Product> {
}
