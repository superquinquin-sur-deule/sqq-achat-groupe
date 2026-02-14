package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record MarkOrderPickedUpCommand(Long orderId) implements Command<Void> {
}
