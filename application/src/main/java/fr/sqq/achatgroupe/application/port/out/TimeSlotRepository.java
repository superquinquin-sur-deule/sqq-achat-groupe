package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

    Optional<TimeSlot> findSlotById(Long id);

    List<TimeSlot> findAvailableByVenteId(Long venteId);

    void save(TimeSlot timeSlot);

    TimeSlot saveNew(TimeSlot timeSlot);

    List<TimeSlot> findAllByVenteId(Long venteId);

    CursorPage<TimeSlot> findAllByVenteId(Long venteId, CursorPageRequest pageRequest);

    void delete(Long id);
}
