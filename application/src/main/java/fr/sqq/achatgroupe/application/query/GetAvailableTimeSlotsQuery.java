package fr.sqq.achatgroupe.application.query;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.Query;

import java.util.List;

public record GetAvailableTimeSlotsQuery(Long venteId) implements Query<List<TimeSlot>> {
}
