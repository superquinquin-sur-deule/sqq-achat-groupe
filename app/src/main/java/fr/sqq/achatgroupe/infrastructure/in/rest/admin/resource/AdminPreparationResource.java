package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import fr.sqq.achatgroupe.application.service.PreparationPdfGenerator;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminPreparationRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.PreparationOrderResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Path("/api/admin/preparation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-preparation")
public class AdminPreparationResource {

    private final Mediator mediator;
    private final PreparationPdfGenerator pdfGenerator;
    private final AdminPreparationRestMapper mapper;

    public AdminPreparationResource(Mediator mediator,
                                    PreparationPdfGenerator pdfGenerator,
                                    AdminPreparationRestMapper mapper) {
        this.mediator = mediator;
        this.pdfGenerator = pdfGenerator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<PreparationOrderResponse>> getPreparationList(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<PreparationOrder> orders = mediator.send(new GeneratePreparationListQuery(venteId));
        return new DataResponse<>(mapper.toResponse(orders));
    }

    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    @Operation(hidden = true)
    public Response getPreparationPdf(@QueryParam("venteId") Long venteId) throws IOException {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<PreparationOrder> orders = mediator.send(new GeneratePreparationListQuery(venteId));
        byte[] pdf = pdfGenerator.generate(orders);
        String filename = "preparation-" + LocalDate.now() + ".pdf";
        return Response.ok(pdf)
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .build();
    }
}
