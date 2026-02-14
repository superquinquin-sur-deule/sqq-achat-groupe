package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.mediator.Command;

public record DeleteProductCommand(ProductId id) implements Command<Void> {
}
