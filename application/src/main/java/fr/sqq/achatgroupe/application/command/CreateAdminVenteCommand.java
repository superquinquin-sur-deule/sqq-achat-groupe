package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.PickupMode;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.Command;

import java.time.Instant;
import java.time.LocalDate;

public record CreateAdminVenteCommand(
        String name,
        String description,
        Instant startDate,
        Instant endDate,
        PickupMode pickupMode,
        LocalDate pickupStartDate,
        LocalDate pickupEndDate
) implements Command<Vente> {
}
