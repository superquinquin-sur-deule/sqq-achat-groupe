package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase;
import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase.SupplierOrderLine;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.SupplierOrderLineResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/backoffice/supplier-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BackofficeSupplierOrderResource {

    private final GenerateSupplierOrderUseCase generateSupplierOrder;

    public BackofficeSupplierOrderResource(GenerateSupplierOrderUseCase generateSupplierOrder) {
        this.generateSupplierOrder = generateSupplierOrder;
    }

    @GET
    public DataResponse<List<SupplierOrderLineResponse>> getSupplierOrder(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<SupplierOrderLine> lines = generateSupplierOrder.generateSupplierOrder(venteId);
        return new DataResponse<>(BackofficeSupplierOrderRestMapper.toResponse(lines));
    }
}
