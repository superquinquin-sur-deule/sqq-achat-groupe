package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.port.in.GetAvailableTimeSlotsUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.TimeSlotRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
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

    private final GetAvailableTimeSlotsUseCase getAvailableTimeSlots;
    private final TimeSlotRestMapper mapper;

    public TimeSlotResource(GetAvailableTimeSlotsUseCase getAvailableTimeSlots, TimeSlotRestMapper mapper) {
        this.getAvailableTimeSlots = getAvailableTimeSlots;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listAvailable(@PathParam("venteId") Long venteId) {
        List<TimeSlotResponse> slots = getAvailableTimeSlots.getAvailableTimeSlots(venteId).stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(slots);
    }
}
