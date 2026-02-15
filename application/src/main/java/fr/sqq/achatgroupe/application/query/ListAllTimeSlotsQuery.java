package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.Query;

public record ListAllTimeSlotsQuery(Long venteId, CursorPageRequest pageRequest) implements Query<CursorPage<TimeSlot>> {

    public static ListAllTimeSlotsQuery all(Long venteId) {
        return new ListAllTimeSlotsQuery(venteId, CursorPageRequest.first(Integer.MAX_VALUE - 1));
    }
}
