package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.ActivateVenteCommand;
import fr.sqq.achatgroupe.application.command.CreateAdminVenteCommand;
import fr.sqq.achatgroupe.application.command.DeactivateVenteCommand;
import fr.sqq.achatgroupe.application.command.DeleteVenteCommand;
import fr.sqq.achatgroupe.application.command.UpdateVenteCommand;
import fr.sqq.achatgroupe.application.query.ListAllVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminVenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateAdminVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateAdminVenteRequest;
import fr.sqq.mediator.Mediator;
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

    private final Mediator mediator;
    private final AdminVenteRestMapper mapper;

    public AdminVenteResource(Mediator mediator, AdminVenteRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<AdminVenteResponse>> listAllVentes() {
        List<Vente> ventes = mediator.send(new ListAllVentesQuery());
        List<AdminVenteResponse> responses = ventes.stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(responses);
    }

    @POST
    public RestResponse<DataResponse<AdminVenteResponse>> createVente(@Valid CreateAdminVenteRequest request) {
        var cmd = new CreateAdminVenteCommand(
                request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = mediator.send(cmd);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(mapper.toResponse(vente)));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<AdminVenteResponse> updateVente(@PathParam("id") Long id, @Valid UpdateAdminVenteRequest request) {
        var cmd = new UpdateVenteCommand(
                new VenteId(id), request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = mediator.send(cmd);
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @DELETE
    @Path("/{id}")
    @APIResponse(responseCode = "204", description = "No content")
    public void deleteVente(@PathParam("id") Long id) {
        mediator.send(new DeleteVenteCommand(new VenteId(id)));
    }

    @PUT
    @Path("/{id}/activate")
    public DataResponse<AdminVenteResponse> activateVente(@PathParam("id") Long id) {
        Vente vente = mediator.send(new ActivateVenteCommand(new VenteId(id)));
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @PUT
    @Path("/{id}/deactivate")
    public DataResponse<AdminVenteResponse> deactivateVente(@PathParam("id") Long id) {
        Vente vente = mediator.send(new DeactivateVenteCommand(new VenteId(id)));
        return new DataResponse<>(mapper.toResponse(vente));
    }
}
