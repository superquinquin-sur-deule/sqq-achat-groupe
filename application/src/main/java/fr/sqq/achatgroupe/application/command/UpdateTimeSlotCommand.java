package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.Command;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateTimeSlotCommand(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int capacity
) implements Command<TimeSlot> {
}
