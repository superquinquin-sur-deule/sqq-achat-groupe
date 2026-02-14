package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.port.in.ManageVentesUseCase;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminVenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateAdminVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateAdminVenteRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/admin/ventes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-ventes")
public class AdminVenteResource {

    private final ManageVentesUseCase manageVentes;
    private final AdminVenteRestMapper mapper;

    public AdminVenteResource(ManageVentesUseCase manageVentes, AdminVenteRestMapper mapper) {
        this.manageVentes = manageVentes;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<AdminVenteResponse>> listAllVentes() {
        List<AdminVenteResponse> ventes = manageVentes.listAllVentes().stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(ventes);
    }

    @POST
    public RestResponse<DataResponse<AdminVenteResponse>> createVente(@Valid CreateAdminVenteRequest request) {
        var cmd = new ManageVentesUseCase.CreateCommand(
                request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = manageVentes.createVente(cmd);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(mapper.toResponse(vente)));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<AdminVenteResponse> updateVente(@PathParam("id") Long id, @Valid UpdateAdminVenteRequest request) {
        var cmd = new ManageVentesUseCase.UpdateCommand(
                new VenteId(id), request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = manageVentes.updateVente(cmd);
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @DELETE
    @Path("/{id}")
    @APIResponse(responseCode = "204", description = "No content")
    public void deleteVente(@PathParam("id") Long id) {
        manageVentes.deleteVente(new VenteId(id));
    }

    @PUT
    @Path("/{id}/activate")
    public DataResponse<AdminVenteResponse> activateVente(@PathParam("id") Long id) {
        Vente vente = manageVentes.activateVente(new VenteId(id));
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @PUT
    @Path("/{id}/deactivate")
    public DataResponse<AdminVenteResponse> deactivateVente(@PathParam("id") Long id) {
        Vente vente = manageVentes.deactivateVente(new VenteId(id));
        return new DataResponse<>(mapper.toResponse(vente));
    }
}
