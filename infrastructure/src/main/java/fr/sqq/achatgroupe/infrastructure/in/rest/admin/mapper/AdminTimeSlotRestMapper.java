package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.command.CreateTimeSlotCommand;
import fr.sqq.achatgroupe.application.command.UpdateTimeSlotCommand;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateTimeSlotRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateTimeSlotRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "cdi")
public interface AdminTimeSlotRestMapper {

    @Mapping(target = "id", expression = "java(timeSlot.id())")
    @Mapping(target = "date", expression = "java(timeSlot.date())")
    @Mapping(target = "startTime", expression = "java(timeSlot.startTime())")
    @Mapping(target = "endTime", expression = "java(timeSlot.endTime())")
    @Mapping(target = "capacity", expression = "java(timeSlot.capacity())")
    @Mapping(target = "reserved", expression = "java(timeSlot.reserved())")
    @Mapping(target = "remainingPlaces", expression = "java(timeSlot.remainingPlaces())")
    TimeSlotResponse toResponse(TimeSlot timeSlot);

    default CreateTimeSlotCommand toCreateCommand(CreateTimeSlotRequest request) {
        return new CreateTimeSlotCommand(
                request.venteId(),
                LocalDate.parse(request.date()),
                LocalTime.parse(request.startTime()),
                LocalTime.parse(request.endTime()),
                request.capacity()
        );
    }

    default UpdateTimeSlotCommand toUpdateCommand(Long id, UpdateTimeSlotRequest request) {
        return new UpdateTimeSlotCommand(
                id,
                LocalDate.parse(request.date()),
                LocalTime.parse(request.startTime()),
                LocalTime.parse(request.endTime()),
                request.capacity()
        );
    }
}
