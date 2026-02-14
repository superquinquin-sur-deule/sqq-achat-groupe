package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.TimeSlotEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.TimeSlotPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TimeSlotPanacheRepository implements TimeSlotRepository, PanacheRepositoryBase<TimeSlotEntity, Long> {

    @Inject
    TimeSlotPersistenceMapper mapper;

    @Override
    public Optional<TimeSlot> findSlotById(Long id) {
        return find("id", id).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<TimeSlot> findAvailableByVenteId(Long venteId) {
        return list("reserved < capacity and date >= ?1 and venteId = ?2 order by date, startTime", LocalDate.now(), venteId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void save(TimeSlot timeSlot) {
        TimeSlotEntity entity = findById(timeSlot.id());
        entity.setDate(timeSlot.date());
        entity.setStartTime(timeSlot.startTime());
        entity.setEndTime(timeSlot.endTime());
        entity.setCapacity(timeSlot.capacity());
        entity.setReserved(timeSlot.reserved());
    }

    @Override
    public TimeSlot saveNew(TimeSlot timeSlot) {
        TimeSlotEntity entity = mapper.toEntity(timeSlot);
        persist(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public List<TimeSlot> findAllByVenteId(Long venteId) {
        return list("venteId = ?1 order by date, startTime", venteId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Long id) {
        deleteById(id);
    }
}
