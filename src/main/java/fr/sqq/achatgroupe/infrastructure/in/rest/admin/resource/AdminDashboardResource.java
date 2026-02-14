package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminDashboardRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-dashboard")
public class AdminDashboardResource {

    private final GetDashboardStatsUseCase getDashboardStats;
    private final ManageTimeSlotsUseCase manageTimeSlots;
    private final AdminDashboardRestMapper mapper;

    public AdminDashboardResource(GetDashboardStatsUseCase getDashboardStats, ManageTimeSlotsUseCase manageTimeSlots, AdminDashboardRestMapper mapper) {
        this.getDashboardStats = getDashboardStats;
        this.manageTimeSlots = manageTimeSlots;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<DashboardStatsResponse> getDashboardStats(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new jakarta.ws.rs.BadRequestException("venteId is required");
        }
        var stats = getDashboardStats.getStats(venteId);
        List<TimeSlot> timeSlots = manageTimeSlots.listAllTimeSlots(venteId);
        return new DataResponse<>(mapper.toResponse(stats, timeSlots));
    }
}
