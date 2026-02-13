package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase.CreateTimeSlotCommand;
import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase.UpdateTimeSlotCommand;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateTimeSlotRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.TimeSlotResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateTimeSlotRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public class AdminTimeSlotRestMapper {

    private AdminTimeSlotRestMapper() {
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

    public static CreateTimeSlotCommand toCreateCommand(CreateTimeSlotRequest request) {
        return new CreateTimeSlotCommand(
                request.venteId(),
                LocalDate.parse(request.date()),
                LocalTime.parse(request.startTime()),
                LocalTime.parse(request.endTime()),
                request.capacity()
        );
    }

    public static UpdateTimeSlotCommand toUpdateCommand(Long id, UpdateTimeSlotRequest request) {
        return new UpdateTimeSlotCommand(
                id,
                LocalDate.parse(request.date()),
                LocalTime.parse(request.startTime()),
                LocalTime.parse(request.endTime()),
                request.capacity()
        );
    }
}
