package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase;
import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase.SupplierOrderLine;
import fr.sqq.achatgroupe.application.service.SupplierOrderExcelGenerator;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.SupplierOrderLineResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Path("/api/backoffice/supplier-orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "backoffice-supplier-orders")
public class BackofficeSupplierOrderResource {

    private final GenerateSupplierOrderUseCase generateSupplierOrder;
    private final SupplierOrderExcelGenerator excelGenerator;

    public BackofficeSupplierOrderResource(GenerateSupplierOrderUseCase generateSupplierOrder,
                                           SupplierOrderExcelGenerator excelGenerator) {
        this.generateSupplierOrder = generateSupplierOrder;
        this.excelGenerator = excelGenerator;
    }

    @GET
    public DataResponse<List<SupplierOrderLineResponse>> getSupplierOrder(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<SupplierOrderLine> lines = generateSupplierOrder.generateSupplierOrder(venteId);
        return new DataResponse<>(BackofficeSupplierOrderRestMapper.toResponse(lines));
    }

    @GET
    @Path("/xlsx")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(hidden = true)
    public Response getSupplierOrderXlsx(@QueryParam("venteId") Long venteId) throws IOException {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<SupplierOrderLine> lines = generateSupplierOrder.generateSupplierOrder(venteId);
        byte[] xlsx = excelGenerator.generate(lines);
        String filename = "bon-fournisseur-" + LocalDate.now() + ".xlsx";
        return Response.ok(xlsx)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .build();
    }
}
