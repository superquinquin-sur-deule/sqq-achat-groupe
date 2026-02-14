package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.command.CreateVenteResult;
import fr.sqq.achatgroupe.application.query.GetVenteQuery;
import fr.sqq.achatgroupe.application.query.ListActiveVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.VenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.VenteListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto.VenteResponse;
import fr.sqq.mediator.Mediator;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

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
    public VenteListResponse listVentes() {
        List<Vente> ventes = mediator.send(new ListActiveVentesQuery());
        List<VenteResponse> responses = ventes.stream()
                .map(mapper::toResponse)
                .toList();
        return new VenteListResponse(responses);
    }

    @GET
    @Path("/{venteId}")
    public DataResponse<VenteResponse> getVente(@PathParam("venteId") Long venteId) {
        Vente vente = mediator.send(new GetVenteQuery(new VenteId(venteId)));
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @POST
    public RestResponse<DataResponse<CreateVenteResponse>> createVente(@Valid CreateVenteRequest request) {
        var command = mapper.toCommand(request);
        CreateVenteResult result = mediator.send(command);
        var response = mapper.toCreateResponse(result);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(response));
    }
}
