package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.TimeSlotResponse;

public class TimeSlotRestMapper {

    private TimeSlotRestMapper() {
    }

    public static TimeSlotResponse toResponse(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.id(),
                timeSlot.date(),
                timeSlot.startTime(),
                timeSlot.endTime(),
                timeSlot.capacity(),
                timeSlot.reserved(),
                timeSlot.remainingPlaces()
        );
    }
}
