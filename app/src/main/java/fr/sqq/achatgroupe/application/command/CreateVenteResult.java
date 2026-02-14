package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.vente.Vente;

import java.util.List;

public record CreateVenteResult(
        Vente vente,
        List<Long> productIds,
        List<Long> timeSlotIds
) {
}
