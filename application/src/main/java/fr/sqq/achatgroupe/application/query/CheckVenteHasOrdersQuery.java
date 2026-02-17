package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

public record CheckVenteHasOrdersQuery(Long venteId) implements Query<Boolean> {
}
