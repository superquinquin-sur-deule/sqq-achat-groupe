package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record CancelOrderCommand(Long orderId) implements Command<Void> {
}
