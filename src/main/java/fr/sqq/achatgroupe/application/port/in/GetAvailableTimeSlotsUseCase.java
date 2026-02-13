package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;

import java.util.List;

public interface GetAvailableTimeSlotsUseCase {

    List<TimeSlot> getAvailableTimeSlots(Long venteId);
}
