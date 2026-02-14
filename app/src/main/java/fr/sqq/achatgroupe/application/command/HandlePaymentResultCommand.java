package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record HandlePaymentResultCommand(
        String payload,
        String signature
) implements Command<Void> {
}
