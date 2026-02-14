package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.query.GetDashboardStatsQuery;
import fr.sqq.achatgroupe.application.query.GetDashboardStatsQuery.DashboardStats;
import fr.sqq.achatgroupe.application.query.ListAllTimeSlotsQuery;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminDashboardRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-dashboard")
public class AdminDashboardResource {

    private final Mediator mediator;
    private final AdminDashboardRestMapper mapper;

    public AdminDashboardResource(Mediator mediator, AdminDashboardRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<DashboardStatsResponse> getDashboardStats(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new jakarta.ws.rs.BadRequestException("venteId is required");
        }
        DashboardStats stats = mediator.send(new GetDashboardStatsQuery(venteId));
        List<TimeSlot> timeSlots = mediator.send(new ListAllTimeSlotsQuery(venteId));
        return new DataResponse<>(mapper.toResponse(stats, timeSlots));
    }
}
