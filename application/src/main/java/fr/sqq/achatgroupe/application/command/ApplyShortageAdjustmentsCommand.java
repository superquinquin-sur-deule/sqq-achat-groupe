package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record ApplyShortageAdjustmentsCommand(Long venteId) implements Command<Void> {
}
