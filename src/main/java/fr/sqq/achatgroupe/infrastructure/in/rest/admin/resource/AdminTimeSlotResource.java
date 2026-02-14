package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminTimeSlotRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateTimeSlotRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateTimeSlotRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin/timeslots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-timeslots")
public class AdminTimeSlotResource {

    private final ManageTimeSlotsUseCase manageTimeSlots;
    private final AdminTimeSlotRestMapper mapper;

    public AdminTimeSlotResource(ManageTimeSlotsUseCase manageTimeSlots, AdminTimeSlotRestMapper mapper) {
        this.manageTimeSlots = manageTimeSlots;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listTimeSlots(@QueryParam("venteId") Long venteId) {
        var timeSlots = manageTimeSlots.listAllTimeSlots(venteId).stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(timeSlots);
    }

    @POST
    public DataResponse<TimeSlotResponse> createTimeSlot(@Valid CreateTimeSlotRequest request) {
        var command = mapper.toCreateCommand(request);
        var timeSlot = manageTimeSlots.createTimeSlot(command);
        return new DataResponse<>(mapper.toResponse(timeSlot));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<TimeSlotResponse> updateTimeSlot(@PathParam("id") Long id, @Valid UpdateTimeSlotRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        var timeSlot = manageTimeSlots.updateTimeSlot(command);
        return new DataResponse<>(mapper.toResponse(timeSlot));
    }

    @DELETE
    @Path("/{id}")
    public void deleteTimeSlot(@PathParam("id") Long id, @QueryParam("force") @DefaultValue("false") boolean force) {
        manageTimeSlots.deleteTimeSlot(id, force);
    }
}
