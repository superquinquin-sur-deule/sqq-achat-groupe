package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase;
import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationOrder;
import fr.sqq.achatgroupe.application.service.PreparationPdfGenerator;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.PreparationOrderResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Path("/api/backoffice/preparation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "backoffice-preparation")
public class BackofficePreparationResource {

    private final GeneratePreparationListUseCase generatePreparationList;
    private final PreparationPdfGenerator pdfGenerator;

    public BackofficePreparationResource(GeneratePreparationListUseCase generatePreparationList,
                                         PreparationPdfGenerator pdfGenerator) {
        this.generatePreparationList = generatePreparationList;
        this.pdfGenerator = pdfGenerator;
    }

    @GET
    public DataResponse<List<PreparationOrderResponse>> getPreparationList(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<PreparationOrder> orders = generatePreparationList.generatePreparationList(venteId);
        return new DataResponse<>(BackofficePreparationRestMapper.toResponse(orders));
    }

    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    @Operation(hidden = true)
    public Response getPreparationPdf(@QueryParam("venteId") Long venteId) throws IOException {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<PreparationOrder> orders = generatePreparationList.generatePreparationList(venteId);
        byte[] pdf = pdfGenerator.generate(orders);
        String filename = "preparation-" + LocalDate.now() + ".pdf";
        return Response.ok(pdf)
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .build();
    }
}
