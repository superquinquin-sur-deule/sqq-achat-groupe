package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.mediator.Command;

public record ActivateVenteCommand(VenteId id) implements Command<Vente> {
}
