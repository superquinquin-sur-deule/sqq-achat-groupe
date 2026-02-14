package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.Query;

import java.util.List;

public record ListActiveVentesQuery() implements Query<List<Vente>> {
}
