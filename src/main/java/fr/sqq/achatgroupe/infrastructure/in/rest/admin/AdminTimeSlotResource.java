package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateTimeSlotRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.TimeSlotResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateTimeSlotRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/admin/timeslots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminTimeSlotResource {

    private final ManageTimeSlotsUseCase manageTimeSlots;

    public AdminTimeSlotResource(ManageTimeSlotsUseCase manageTimeSlots) {
        this.manageTimeSlots = manageTimeSlots;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listTimeSlots(@QueryParam("venteId") Long venteId) {
        var timeSlots = manageTimeSlots.listAllTimeSlots(venteId).stream()
                .map(AdminTimeSlotRestMapper::toResponse)
                .toList();
        return new DataResponse<>(timeSlots);
    }

    @POST
    public DataResponse<TimeSlotResponse> createTimeSlot(@Valid CreateTimeSlotRequest request) {
        var command = AdminTimeSlotRestMapper.toCreateCommand(request);
        var timeSlot = manageTimeSlots.createTimeSlot(command);
        return new DataResponse<>(AdminTimeSlotRestMapper.toResponse(timeSlot));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<TimeSlotResponse> updateTimeSlot(@PathParam("id") Long id, @Valid UpdateTimeSlotRequest request) {
        var command = AdminTimeSlotRestMapper.toUpdateCommand(id, request);
        var timeSlot = manageTimeSlots.updateTimeSlot(command);
        return new DataResponse<>(AdminTimeSlotRestMapper.toResponse(timeSlot));
    }

    @DELETE
    @Path("/{id}")
    public void deleteTimeSlot(@PathParam("id") Long id, @QueryParam("force") @DefaultValue("false") boolean force) {
        manageTimeSlots.deleteTimeSlot(id, force);
    }
}
