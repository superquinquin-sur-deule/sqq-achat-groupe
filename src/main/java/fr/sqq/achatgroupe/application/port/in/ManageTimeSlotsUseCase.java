package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ManageTimeSlotsUseCase {

    List<TimeSlot> listAllTimeSlots(Long venteId);

    TimeSlot createTimeSlot(CreateTimeSlotCommand command);

    TimeSlot updateTimeSlot(UpdateTimeSlotCommand command);

    void deleteTimeSlot(Long id, boolean force);

    record CreateTimeSlotCommand(
            Long venteId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int capacity
    ) {}

    record UpdateTimeSlotCommand(
            Long id,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int capacity
    ) {}
}
