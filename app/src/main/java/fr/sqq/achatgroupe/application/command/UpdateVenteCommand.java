package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.mediator.Command;

import java.time.Instant;

public record UpdateVenteCommand(
        VenteId id,
        String name,
        String description,
        Instant startDate,
        Instant endDate
) implements Command<Vente> {
}
