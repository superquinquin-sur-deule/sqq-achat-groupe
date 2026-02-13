package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase;
import fr.sqq.achatgroupe.application.port.in.GetVenteUseCase;
import fr.sqq.achatgroupe.application.port.in.ListVentesUseCase;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.VenteListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.VenteResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/ventes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenteResource {

    private final ListVentesUseCase listVentesUseCase;
    private final GetVenteUseCase getVenteUseCase;
    private final CreateVenteUseCase createVenteUseCase;

    public VenteResource(ListVentesUseCase listVentesUseCase, GetVenteUseCase getVenteUseCase, CreateVenteUseCase createVenteUseCase) {
        this.listVentesUseCase = listVentesUseCase;
        this.getVenteUseCase = getVenteUseCase;
        this.createVenteUseCase = createVenteUseCase;
    }

    @GET
    public VenteListResponse listVentes() {
        List<VenteResponse> ventes = listVentesUseCase.listActiveVentes().stream()
                .map(VenteRestMapper::toResponse)
                .toList();
        return new VenteListResponse(ventes);
    }

    @GET
    @Path("/{venteId}")
    public DataResponse<VenteResponse> getVente(@PathParam("venteId") Long venteId) {
        Vente vente = getVenteUseCase.getVente(new VenteId(venteId));
        return new DataResponse<>(VenteRestMapper.toResponse(vente));
    }

    @POST
    public Response createVente(@Valid CreateVenteRequest request) {
        var command = VenteRestMapper.toCommand(request);
        var result = createVenteUseCase.execute(command);
        var response = VenteRestMapper.toCreateResponse(result);
        return Response.status(Response.Status.CREATED)
                .entity(new DataResponse<>(response))
                .build();
    }
}
