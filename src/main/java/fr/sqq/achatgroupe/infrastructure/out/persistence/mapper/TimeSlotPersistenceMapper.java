package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.TimeSlotEntity;

public class TimeSlotPersistenceMapper {

    private TimeSlotPersistenceMapper() {
    }

    public static TimeSlot toDomain(TimeSlotEntity entity) {
        return new TimeSlot(
                entity.getId(),
                entity.getVenteId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getCapacity(),
                entity.getReserved()
        );
    }

    public static TimeSlotEntity toEntity(TimeSlot domain) {
        var entity = new TimeSlotEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setDate(domain.date());
        entity.setStartTime(domain.startTime());
        entity.setEndTime(domain.endTime());
        entity.setCapacity(domain.capacity());
        entity.setReserved(domain.reserved());
        return entity;
    }
}
