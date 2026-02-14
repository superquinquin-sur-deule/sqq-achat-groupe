package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.util.UUID;

public record MarkOrderPickedUpCommand(UUID orderId) implements Command<Void> {
}
