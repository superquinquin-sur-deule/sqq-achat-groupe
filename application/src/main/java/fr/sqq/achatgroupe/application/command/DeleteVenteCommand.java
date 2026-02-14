package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.mediator.Command;

public record DeleteVenteCommand(VenteId id) implements Command<Void> {
}
