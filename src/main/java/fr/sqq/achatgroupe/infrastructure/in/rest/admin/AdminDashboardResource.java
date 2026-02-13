package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.GetDashboardStatsUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DashboardStatsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/admin/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminDashboardResource {

    private final GetDashboardStatsUseCase getDashboardStats;
    private final ManageTimeSlotsUseCase manageTimeSlots;

    public AdminDashboardResource(GetDashboardStatsUseCase getDashboardStats, ManageTimeSlotsUseCase manageTimeSlots) {
        this.getDashboardStats = getDashboardStats;
        this.manageTimeSlots = manageTimeSlots;
    }

    @GET
    public DataResponse<DashboardStatsResponse> getDashboardStats(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new jakarta.ws.rs.BadRequestException("venteId is required");
        }
        var stats = getDashboardStats.getStats(venteId);
        List<TimeSlot> timeSlots = manageTimeSlots.listAllTimeSlots(venteId);
        return new DataResponse<>(AdminDashboardRestMapper.toResponse(stats, timeSlots));
    }
}
