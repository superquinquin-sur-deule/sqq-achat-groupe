package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.application.port.in.GetAvailableTimeSlotsUseCase;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetAvailableTimeSlots implements GetAvailableTimeSlotsUseCase {

    private final TimeSlotRepository timeSlotRepository;

    public GetAvailableTimeSlots(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlots(Long venteId) {
        return timeSlotRepository.findAvailableByVenteId(venteId);
    }
}
