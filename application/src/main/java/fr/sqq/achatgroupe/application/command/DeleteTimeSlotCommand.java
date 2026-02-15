package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

public record DeleteTimeSlotCommand(Long venteId, Long id, boolean force) implements Command<Void> {
}
