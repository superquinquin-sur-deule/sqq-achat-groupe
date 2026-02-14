package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.query.GetAvailableTimeSlotsQuery;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.TimeSlotRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/ventes/{venteId}/timeslots")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "timeslots")
public class TimeSlotResource {

    private final Mediator mediator;
    private final TimeSlotRestMapper mapper;

    public TimeSlotResource(Mediator mediator, TimeSlotRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listAvailable(@PathParam("venteId") Long venteId) {
        List<TimeSlot> timeSlots = mediator.send(new GetAvailableTimeSlotsQuery(venteId));
        List<TimeSlotResponse> slots = timeSlots.stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(slots);
    }
}
