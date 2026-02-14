package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface TimeSlotRestMapper {

    @Mapping(target = "id", expression = "java(timeSlot.id())")
    @Mapping(target = "date", expression = "java(timeSlot.date())")
    @Mapping(target = "startTime", expression = "java(timeSlot.startTime())")
    @Mapping(target = "endTime", expression = "java(timeSlot.endTime())")
    @Mapping(target = "capacity", expression = "java(timeSlot.capacity())")
    @Mapping(target = "reserved", expression = "java(timeSlot.reserved())")
    @Mapping(target = "remainingPlaces", expression = "java(timeSlot.remainingPlaces())")
    TimeSlotResponse toResponse(TimeSlot timeSlot);
}
