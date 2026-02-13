package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase.ImportProductsCommand;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.CsvProductParser.CsvParseResult;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.AdminProductResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateProductRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ImportProductsResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ImportProductsResponse.ImportErrorDetail;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ProblemDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Path("/api/admin/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminProductResource {

    private final ManageProductsUseCase manageProducts;
    private final CsvProductParser csvProductParser;

    public AdminProductResource(ManageProductsUseCase manageProducts, CsvProductParser csvProductParser) {
        this.manageProducts = manageProducts;
        this.csvProductParser = csvProductParser;
    }

    @GET
    public DataResponse<List<AdminProductResponse>> listProducts(@QueryParam("venteId") Long venteId) {
        var products = manageProducts.listAllProducts(venteId).stream()
                .map(AdminProductRestMapper::toResponse)
                .toList();
        return new DataResponse<>(products);
    }

    @POST
    public DataResponse<AdminProductResponse> createProduct(@Valid CreateProductRequest request) {
        var command = AdminProductRestMapper.toCreateCommand(request);
        var product = manageProducts.createProduct(command);
        return new DataResponse<>(AdminProductRestMapper.toResponse(product));
    }

    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
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
            var result = manageProducts.importProducts(command);

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
        var command = AdminProductRestMapper.toUpdateCommand(id, request);
        var product = manageProducts.updateProduct(command);
        return new DataResponse<>(AdminProductRestMapper.toResponse(product));
    }

    @DELETE
    @Path("/{id}")
    public void deleteProduct(@PathParam("id") Long id) {
        manageProducts.deleteProduct(new ProductId(id));
    }
}
