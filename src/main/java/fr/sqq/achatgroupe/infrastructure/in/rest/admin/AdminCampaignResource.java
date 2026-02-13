package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageCampaignUseCase;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CampaignResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateCampaignRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/admin/campaign")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminCampaignResource {

    private final ManageCampaignUseCase manageCampaign;

    public AdminCampaignResource(ManageCampaignUseCase manageCampaign) {
        this.manageCampaign = manageCampaign;
    }

    @GET
    public DataResponse<CampaignResponse> getCampaignStatus(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new jakarta.ws.rs.BadRequestException("venteId is required");
        }
        Vente vente = manageCampaign.getCampaignStatus(new VenteId(venteId));
        return new DataResponse<>(AdminCampaignRestMapper.toResponse(vente));
    }

    @GET
    @Path("/ventes")
    public DataResponse<List<CampaignResponse>> listAllVentes() {
        List<CampaignResponse> ventes = manageCampaign.listAllVentes().stream()
                .map(AdminCampaignRestMapper::toResponse)
                .toList();
        return new DataResponse<>(ventes);
    }

    @PUT
    public DataResponse<CampaignResponse> updateCampaign(@Valid UpdateCampaignRequest request) {
        VenteId venteId = new VenteId(request.venteId());
        Vente vente;
        if (Boolean.TRUE.equals(request.active())) {
            vente = manageCampaign.activateCampaign(venteId);
        } else {
            vente = manageCampaign.deactivateCampaign(venteId);
        }
        return new DataResponse<>(AdminCampaignRestMapper.toResponse(vente));
    }
}
