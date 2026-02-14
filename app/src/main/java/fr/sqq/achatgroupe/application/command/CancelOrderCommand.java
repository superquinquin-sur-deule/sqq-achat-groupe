package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.util.UUID;

public record CancelOrderCommand(UUID orderId) implements Command<Void> {
}
