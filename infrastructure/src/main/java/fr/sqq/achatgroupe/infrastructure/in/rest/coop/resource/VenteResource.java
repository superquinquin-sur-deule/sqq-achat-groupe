package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.application.query.GetVenteQuery;
import fr.sqq.achatgroupe.application.query.ListActiveVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.VenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CursorPageResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto.VenteResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/ventes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "ventes")
public class VenteResource {

    private final Mediator mediator;
    private final VenteRestMapper mapper;

    public VenteResource(Mediator mediator, VenteRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public CursorPageResponse<VenteResponse> listVentes(
            @QueryParam("cursor") String cursor,
            @QueryParam("size") @DefaultValue("50") int size) {
        var pageRequest = cursor != null ? CursorPageRequest.after(cursor, size) : CursorPageRequest.first(size);
        CursorPage<Vente> page = mediator.send(new ListActiveVentesQuery(pageRequest));
        List<VenteResponse> responses = page.items().stream()
                .map(mapper::toResponse)
                .toList();
        return new CursorPageResponse<>(responses, new CursorPageResponse.PageInfo(page.endCursor(), page.hasNext()));
    }

    @GET
    @Path("/{venteId}")
    public DataResponse<VenteResponse> getVente(@PathParam("venteId") Long venteId) {
        Vente vente = mediator.send(new GetVenteQuery(new VenteId(venteId)));
        return new DataResponse<>(mapper.toResponse(vente));
    }
}
