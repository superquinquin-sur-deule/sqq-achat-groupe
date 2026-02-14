package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.TimeSlotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface TimeSlotPersistenceMapper {

    TimeSlot toDomain(TimeSlotEntity entity);

    @Mapping(target = "id", expression = "java(domain.id())")
    @Mapping(target = "venteId", expression = "java(domain.venteId())")
    @Mapping(target = "date", expression = "java(domain.date())")
    @Mapping(target = "startTime", expression = "java(domain.startTime())")
    @Mapping(target = "endTime", expression = "java(domain.endTime())")
    @Mapping(target = "capacity", expression = "java(domain.capacity())")
    @Mapping(target = "reserved", expression = "java(domain.reserved())")
    TimeSlotEntity toEntity(TimeSlot domain);
}
