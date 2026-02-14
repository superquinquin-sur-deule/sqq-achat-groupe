package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.DeleteProductCommand;
import fr.sqq.achatgroupe.application.command.ImportProductsCommand;
import fr.sqq.achatgroupe.application.command.ImportResult;
import fr.sqq.achatgroupe.application.query.ListAllProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminProductRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.util.CsvProductParser;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.util.CsvProductParser.CsvParseResult;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateProductRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ImportProductsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ImportProductsResponse.ImportErrorDetail;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProblemDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.UpdateProductRequest;
import fr.sqq.mediator.Mediator;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Path("/api/admin/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-products")
public class AdminProductResource {

    private final Mediator mediator;
    private final CsvProductParser csvProductParser;
    private final AdminProductRestMapper mapper;

    public AdminProductResource(Mediator mediator, CsvProductParser csvProductParser, AdminProductRestMapper mapper) {
        this.mediator = mediator;
        this.csvProductParser = csvProductParser;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<AdminProductResponse>> listProducts(@QueryParam("venteId") Long venteId) {
        List<Product> products = mediator.send(new ListAllProductsQuery(venteId));
        var responses = products.stream()
                .map(mapper::toResponse)
                .toList();
        return new DataResponse<>(responses);
    }

    @POST
    public DataResponse<AdminProductResponse> createProduct(@Valid CreateProductRequest request) {
        var command = mapper.toCreateCommand(request);
        Product product = mediator.send(command);
        return new DataResponse<>(mapper.toResponse(product));
    }

    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @APIResponse(responseCode = "200", description = "Import result")
    @APIResponse(responseCode = "400", description = "Invalid CSV file")
    public Response importProducts(
            @FormParam("file") FileUpload file,
            @FormParam("venteId") Long venteId
    ) throws IOException {
        if (file == null || file.fileName() == null) {
            return Response.status(400)
                    .type(MediaType.valueOf("application/problem+json"))
                    .entity(new ProblemDetailResponse(
                            "https://api.sqq.fr/errors/invalid-csv",
                            "Format de fichier invalide",
                            400,
                            "Aucun fichier fourni"))
                    .build();
        }

        String contentType = file.contentType();
        String fileName = file.fileName();
        boolean isCsv = "text/csv".equals(contentType)
                || "application/octet-stream".equals(contentType)
                || (fileName != null && fileName.toLowerCase().endsWith(".csv"));

        if (!isCsv) {
            return Response.status(400)
                    .type(MediaType.valueOf("application/problem+json"))
                    .entity(new ProblemDetailResponse(
                            "https://api.sqq.fr/errors/invalid-csv",
                            "Format de fichier invalide",
                            400,
                            "Le fichier doit être au format CSV avec les colonnes : nom, description, prix, fournisseur, stock"))
                    .build();
        }

        try (InputStream inputStream = Files.newInputStream(file.filePath())) {
            CsvParseResult parseResult = csvProductParser.parse(inputStream);

            if (parseResult.hasGlobalError()) {
                return Response.status(400)
                        .type(MediaType.valueOf("application/problem+json"))
                        .entity(new ProblemDetailResponse(
                                "https://api.sqq.fr/errors/invalid-csv",
                                "Format de fichier invalide",
                                400,
                                parseResult.globalError()))
                        .build();
            }

            List<ImportErrorDetail> allErrors = new ArrayList<>();

            for (var parseError : parseResult.errors()) {
                allErrors.add(new ImportErrorDetail(parseError.line(), parseError.reason()));
            }

            var command = new ImportProductsCommand(venteId, parseResult.rows());
            ImportResult result = mediator.send(command);

            for (var importError : result.errors()) {
                allErrors.add(new ImportErrorDetail(importError.line(), importError.reason()));
            }

            var response = new ImportProductsResponse(result.imported(), allErrors.size(), allErrors);
            return Response.ok(new DataResponse<>(response)).build();
        }
    }

    @PUT
    @Path("/{id}")
    public DataResponse<AdminProductResponse> updateProduct(@PathParam("id") Long id, @Valid UpdateProductRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        Product product = mediator.send(command);
        return new DataResponse<>(mapper.toResponse(product));
    }

    @DELETE
    @Path("/{id}")
    public void deleteProduct(@PathParam("id") Long id) {
        mediator.send(new DeleteProductCommand(new ProductId(id)));
    }
}
