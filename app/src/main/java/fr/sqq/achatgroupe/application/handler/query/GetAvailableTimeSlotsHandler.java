package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.GetAvailableTimeSlotsQuery;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetAvailableTimeSlotsHandler implements QueryHandler<GetAvailableTimeSlotsQuery, List<TimeSlot>> {

    private final TimeSlotRepository timeSlotRepository;

    public GetAvailableTimeSlotsHandler(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public List<TimeSlot> handle(GetAvailableTimeSlotsQuery query) {
        return timeSlotRepository.findAvailableByVenteId(query.venteId());
    }
}
