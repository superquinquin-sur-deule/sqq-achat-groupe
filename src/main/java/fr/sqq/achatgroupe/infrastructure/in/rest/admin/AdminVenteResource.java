package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageVentesUseCase;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.AdminVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateAdminVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateAdminVenteRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/admin/ventes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminVenteResource {

    private final ManageVentesUseCase manageVentes;

    public AdminVenteResource(ManageVentesUseCase manageVentes) {
        this.manageVentes = manageVentes;
    }

    @GET
    public DataResponse<List<AdminVenteResponse>> listAllVentes() {
        List<AdminVenteResponse> ventes = manageVentes.listAllVentes().stream()
                .map(AdminVenteRestMapper::toResponse)
                .toList();
        return new DataResponse<>(ventes);
    }

    @POST
    public Response createVente(@Valid CreateAdminVenteRequest request) {
        var cmd = new ManageVentesUseCase.CreateCommand(
                request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = manageVentes.createVente(cmd);
        return Response.status(Response.Status.CREATED)
                .entity(new DataResponse<>(AdminVenteRestMapper.toResponse(vente)))
                .build();
    }

    @PUT
    @Path("/{id}")
    public DataResponse<AdminVenteResponse> updateVente(@PathParam("id") Long id, @Valid UpdateAdminVenteRequest request) {
        var cmd = new ManageVentesUseCase.UpdateCommand(
                new VenteId(id), request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = manageVentes.updateVente(cmd);
        return new DataResponse<>(AdminVenteRestMapper.toResponse(vente));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteVente(@PathParam("id") Long id) {
        manageVentes.deleteVente(new VenteId(id));
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/activate")
    public DataResponse<AdminVenteResponse> activateVente(@PathParam("id") Long id) {
        Vente vente = manageVentes.activateVente(new VenteId(id));
        return new DataResponse<>(AdminVenteRestMapper.toResponse(vente));
    }

    @PUT
    @Path("/{id}/deactivate")
    public DataResponse<AdminVenteResponse> deactivateVente(@PathParam("id") Long id) {
        Vente vente = manageVentes.deactivateVente(new VenteId(id));
        return new DataResponse<>(AdminVenteRestMapper.toResponse(vente));
    }
}
