package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import fr.sqq.mediator.Query;

public record ListPreparationOrdersQuery(Long venteId, CursorPageRequest pageRequest, Long timeSlotId)
        implements Query<CursorPage<PreparationOrder>> {
}
