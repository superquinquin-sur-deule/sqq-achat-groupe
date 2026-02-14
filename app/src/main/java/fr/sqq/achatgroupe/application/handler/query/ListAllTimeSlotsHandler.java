package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.query.ListAllTimeSlotsQuery;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListAllTimeSlotsHandler implements QueryHandler<ListAllTimeSlotsQuery, List<TimeSlot>> {

    private final TimeSlotRepository timeSlotRepository;

    public ListAllTimeSlotsHandler(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public List<TimeSlot> handle(ListAllTimeSlotsQuery query) {
        return timeSlotRepository.findAllByVenteId(query.venteId());
    }
}
