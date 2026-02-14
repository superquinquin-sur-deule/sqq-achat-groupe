package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int capacity,
        int reserved,
        int remainingPlaces
) {
}
