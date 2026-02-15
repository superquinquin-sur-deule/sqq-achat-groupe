package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.Query;

public record ListAllVentesQuery(CursorPageRequest pageRequest) implements Query<CursorPage<Vente>> {
}
