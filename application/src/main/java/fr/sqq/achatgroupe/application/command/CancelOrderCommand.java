package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.util.UUID;

public record CancelOrderCommand(UUID orderId, String reason) implements Command<Void> {
    public CancelOrderCommand(UUID orderId) {
        this(orderId, null);
    }
}
