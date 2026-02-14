package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase;
import fr.sqq.achatgroupe.application.port.in.GetVenteUseCase;
import fr.sqq.achatgroupe.application.port.in.ListVentesUseCase;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.VenteRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.VenteListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto.VenteResponse;
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

    private final ListVentesUseCase listVentesUseCase;
    private final GetVenteUseCase getVenteUseCase;
    private final CreateVenteUseCase createVenteUseCase;
    private final VenteRestMapper mapper;

    public VenteResource(ListVentesUseCase listVentesUseCase, GetVenteUseCase getVenteUseCase, CreateVenteUseCase createVenteUseCase, VenteRestMapper mapper) {
        this.listVentesUseCase = listVentesUseCase;
        this.getVenteUseCase = getVenteUseCase;
        this.createVenteUseCase = createVenteUseCase;
        this.mapper = mapper;
    }

    @GET
    public VenteListResponse listVentes() {
        List<VenteResponse> ventes = listVentesUseCase.listActiveVentes().stream()
                .map(mapper::toResponse)
                .toList();
        return new VenteListResponse(ventes);
    }

    @GET
    @Path("/{venteId}")
    public DataResponse<VenteResponse> getVente(@PathParam("venteId") Long venteId) {
        Vente vente = getVenteUseCase.getVente(new VenteId(venteId));
        return new DataResponse<>(mapper.toResponse(vente));
    }

    @POST
    public RestResponse<DataResponse<CreateVenteResponse>> createVente(@Valid CreateVenteRequest request) {
        var command = mapper.toCommand(request);
        var result = createVenteUseCase.execute(command);
        var response = mapper.toCreateResponse(result);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(response));
    }
}
