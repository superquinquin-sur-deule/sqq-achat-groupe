package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.TimeSlotHasReservationsException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ManageTimeSlots implements ManageTimeSlotsUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public ManageTimeSlots(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public List<TimeSlot> listAllTimeSlots(Long venteId) {
        return timeSlotRepository.findAllByVenteId(venteId);
    }

    @Override
    @Transactional
    public TimeSlot createTimeSlot(CreateTimeSlotCommand command) {
        TimeSlot timeSlot = new TimeSlot(
                null,
                command.venteId(),
                command.date(),
                command.startTime(),
                command.endTime(),
                command.capacity(),
                0
        );
        return timeSlotRepository.saveNew(timeSlot);
    }

    @Override
    @Transactional
    public TimeSlot updateTimeSlot(UpdateTimeSlotCommand command) {
        TimeSlot existing = timeSlotRepository.findSlotById(command.id())
                .orElseThrow(() -> new TimeSlotNotFoundException(command.id()));

        TimeSlot updated = new TimeSlot(
                existing.id(),
                existing.venteId(),
                command.date(),
                command.startTime(),
                command.endTime(),
                command.capacity(),
                existing.reserved()
        );
        timeSlotRepository.save(updated);
        return updated;
    }

    @Override
    @Transactional
    public void deleteTimeSlot(Long id, boolean force) {
        TimeSlot existing = timeSlotRepository.findSlotById(id)
                .orElseThrow(() -> new TimeSlotNotFoundException(id));

        if (existing.reserved() > 0 && !force) {
            throw new TimeSlotHasReservationsException(id, existing.reserved());
        }

        timeSlotRepository.delete(id);
    }
}
