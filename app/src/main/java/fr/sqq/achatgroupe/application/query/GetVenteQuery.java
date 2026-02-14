package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.mediator.Query;

public record GetVenteQuery(VenteId id) implements Query<Vente> {
}
