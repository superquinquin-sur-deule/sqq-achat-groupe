package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.Command;

import java.time.Instant;

public record CreateAdminVenteCommand(
        String name,
        String description,
        Instant startDate,
        Instant endDate
) implements Command<Vente> {
}
