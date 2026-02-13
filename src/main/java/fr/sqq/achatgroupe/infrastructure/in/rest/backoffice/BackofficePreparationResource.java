package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase;
import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationOrder;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.PreparationOrderResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/backoffice/preparation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BackofficePreparationResource {

    private final GeneratePreparationListUseCase generatePreparationList;

    public BackofficePreparationResource(GeneratePreparationListUseCase generatePreparationList) {
        this.generatePreparationList = generatePreparationList;
    }

    @GET
    public DataResponse<List<PreparationOrderResponse>> getPreparationList(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<PreparationOrder> orders = generatePreparationList.generatePreparationList(venteId);
        return new DataResponse<>(BackofficePreparationRestMapper.toResponse(orders));
    }
}
