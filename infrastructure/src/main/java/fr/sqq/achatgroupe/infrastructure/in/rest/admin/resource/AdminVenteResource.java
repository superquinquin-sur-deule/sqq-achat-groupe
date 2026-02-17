package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.ActivateVenteCommand;
import fr.sqq.achatgroupe.application.command.CreateAdminVenteCommand;
import fr.sqq.achatgroupe.application.command.DeactivateVenteCommand;
import fr.sqq.achatgroupe.application.command.DeleteVenteCommand;
import fr.sqq.achatgroupe.application.command.UpdateVenteCommand;
import fr.sqq.achatgroupe.application.query.CheckVenteHasOrdersQuery;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.application.query.ListAllVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminVenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateAdminVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CursorPageResponse;
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
    public CursorPageResponse<AdminVenteResponse> listAllVentes(
            @QueryParam("cursor") String cursor,
            @QueryParam("size") @DefaultValue("20") int size) {
        var pageRequest = cursor != null ? CursorPageRequest.after(cursor, size) : CursorPageRequest.first(size);
        CursorPage<Vente> page = mediator.send(new ListAllVentesQuery(pageRequest));
        List<AdminVenteResponse> responses = page.items().stream()
                .map(this::toResponseWithHasOrders)
                .toList();
        return new CursorPageResponse<>(responses, new CursorPageResponse.PageInfo(page.endCursor(), page.hasNext()));
    }

    @POST
    public RestResponse<DataResponse<AdminVenteResponse>> createVente(@Valid CreateAdminVenteRequest request) {
        var cmd = new CreateAdminVenteCommand(
                request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = mediator.send(cmd);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(mapper.toResponse(vente, false)));
    }

    @PUT
    @Path("/{id}")
    public DataResponse<AdminVenteResponse> updateVente(@PathParam("id") Long id, @Valid UpdateAdminVenteRequest request) {
        var cmd = new UpdateVenteCommand(
                new VenteId(id), request.name(), request.description(), request.startDate(), request.endDate());
        Vente vente = mediator.send(cmd);
        return new DataResponse<>(toResponseWithHasOrders(vente));
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
        return new DataResponse<>(toResponseWithHasOrders(vente));
    }

    @PUT
    @Path("/{id}/deactivate")
    public DataResponse<AdminVenteResponse> deactivateVente(@PathParam("id") Long id) {
        Vente vente = mediator.send(new DeactivateVenteCommand(new VenteId(id)));
        return new DataResponse<>(toResponseWithHasOrders(vente));
    }

    private AdminVenteResponse toResponseWithHasOrders(Vente vente) {
        boolean hasOrders = mediator.send(new CheckVenteHasOrdersQuery(vente.id()));
        return mapper.toResponse(vente, hasOrders);
    }

    @GET
    @Path("/{id}/has-orders")
    public DataResponse<Boolean> hasOrders(@PathParam("id") Long id) {
        Boolean result = mediator.send(new CheckVenteHasOrdersQuery(id));
        return new DataResponse<>(result);
    }
}
