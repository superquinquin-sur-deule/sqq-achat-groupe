package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.application.port.in.BrowseCatalogUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.ProductRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/ventes/{venteId}/products")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "products")
public class ProductResource {

    private final BrowseCatalogUseCase browseCatalog;
    private final ProductRestMapper mapper;

    public ProductResource(BrowseCatalogUseCase browseCatalog, ProductRestMapper mapper) {
        this.browseCatalog = browseCatalog;
        this.mapper = mapper;
    }

    @GET
    public ProductListResponse listProducts(@PathParam("venteId") Long venteId) {
        List<ProductResponse> products = browseCatalog.listActiveProducts(venteId).stream()
                .map(mapper::toResponse)
                .toList();

        var meta = new ProductListResponse.Meta(products.size(), 1, products.size());
        return new ProductListResponse(products, meta);
    }

    @GET
    @Path("/{id}")
    public DataResponse<ProductResponse> getProduct(@PathParam("venteId") Long venteId, @PathParam("id") Long id) {
        var product = browseCatalog.getProduct(new ProductId(id));
        var response = mapper.toResponse(product);
        return new DataResponse<>(response);
    }
}
