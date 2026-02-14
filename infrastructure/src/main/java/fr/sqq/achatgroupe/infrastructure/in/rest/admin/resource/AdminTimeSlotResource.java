package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.DeleteTimeSlotCommand;
import fr.sqq.achatgroupe.application.query.ListAllTimeSlotsQuery;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminTimeSlotRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateTimeSlotRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.TimeSlotResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateTimeSlotRequest;
import fr.sqq.mediator.Mediator;
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

    private final Mediator mediator;
    private final AdminTimeSlotRestMapper mapper;

    public AdminTimeSlotResource(Mediator mediator, AdminTimeSlotRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<TimeSlotResponse>> listTimeSlots(@QueryParam("venteId") Long venteId) {
        List<TimeSlot> timeSlots = mediator.send(new ListAllTimeSlotsQuery(venteId));
        var responses = timeSlots.stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(responses);
    }

    @POST
    public DataResponse<TimeSlotResponse> createTimeSlot(@Valid CreateTimeSlotRequest request) {
        var command = mapper.toCreateCommand(request);
        TimeSlot timeSlot = mediator.send(command);
        return new DataResponse<>(mapper.toResponse(timeSlot));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<TimeSlotResponse> updateTimeSlot(@PathParam("id") Long id, @Valid UpdateTimeSlotRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        TimeSlot timeSlot = mediator.send(command);
        return new DataResponse<>(mapper.toResponse(timeSlot));
    }

    @DELETE
    @Path("/{id}")
    public void deleteTimeSlot(@PathParam("id") Long id, @QueryParam("force") @DefaultValue("false") boolean force) {
        mediator.send(new DeleteTimeSlotCommand(id, force));
    }
}
