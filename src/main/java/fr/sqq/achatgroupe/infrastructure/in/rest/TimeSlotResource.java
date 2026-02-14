package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.application.port.in.GetAvailableTimeSlotsUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.TimeSlotResponse;
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

    public TimeSlotResource(GetAvailableTimeSlotsUseCase getAvailableTimeSlots) {
        this.getAvailableTimeSlots = getAvailableTimeSlots;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listAvailable(@PathParam("venteId") Long venteId) {
        List<TimeSlotResponse> slots = getAvailableTimeSlots.getAvailableTimeSlots(venteId).stream()
                .map(TimeSlotRestMapper::toResponse)
                .toList();
        return new DataResponse<>(slots);
    }
}
