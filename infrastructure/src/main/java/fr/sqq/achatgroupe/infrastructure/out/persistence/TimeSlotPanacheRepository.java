package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.infrastructure.out.persistence.cursor.CursorCodec;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.TimeSlotEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.TimeSlotPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
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
    public CursorPage<TimeSlot> findAllByVenteId(Long venteId, CursorPageRequest pageRequest) {
        var params = new HashMap<String, Object>();
        params.put("venteId", venteId);
        String query;

        if (pageRequest.cursor() != null) {
            LocalDate cursorDate = CursorCodec.decodeTimeSlotCursorDate(pageRequest.cursor());
            LocalTime cursorStartTime = CursorCodec.decodeTimeSlotCursorStartTime(pageRequest.cursor());
            Long cursorId = CursorCodec.decodeTimeSlotCursorId(pageRequest.cursor());
            query = "venteId = :venteId AND (date > :cursorDate OR (date = :cursorDate AND startTime > :cursorStartTime) OR (date = :cursorDate AND startTime = :cursorStartTime AND id > :cursorId)) ORDER BY date ASC, startTime ASC, id ASC";
            params.put("cursorDate", cursorDate);
            params.put("cursorStartTime", cursorStartTime);
            params.put("cursorId", cursorId);
        } else {
            query = "venteId = :venteId ORDER BY date ASC, startTime ASC, id ASC";
        }

        int fetchSize = pageRequest.size() + 1;
        List<TimeSlotEntity> entities = find(query, params).range(0, fetchSize - 1).list();
        return buildCursorPage(entities, pageRequest.size());
    }

    @Override
    public void delete(Long id) {
        deleteById(id);
    }

    private CursorPage<TimeSlot> buildCursorPage(List<TimeSlotEntity> entities, int size) {
        boolean hasNext = entities.size() > size;
        List<TimeSlotEntity> page = hasNext ? entities.subList(0, size) : entities;
        List<TimeSlot> items = page.stream().map(mapper::toDomain).toList();

        String endCursor = null;
        if (!page.isEmpty()) {
            TimeSlotEntity last = page.get(page.size() - 1);
            endCursor = CursorCodec.encodeTimeSlotCursor(last.getDate(), last.getStartTime(), last.getId());
        }

        return new CursorPage<>(items, endCursor, hasNext);
    }
}
