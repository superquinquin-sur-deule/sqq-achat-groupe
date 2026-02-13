package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.application.port.in.BrowseCatalogUseCase;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ProductListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ProductResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/ventes/{venteId}/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final BrowseCatalogUseCase browseCatalog;

    public ProductResource(BrowseCatalogUseCase browseCatalog) {
        this.browseCatalog = browseCatalog;
    }

    @GET
    public ProductListResponse listProducts(@PathParam("venteId") Long venteId) {
        List<ProductResponse> products = browseCatalog.listActiveProducts(venteId).stream()
                .map(ProductRestMapper::toResponse)
                .toList();

        var meta = new ProductListResponse.Meta(products.size(), 1, products.size());
        return new ProductListResponse(products, meta);
    }

    @GET
    @Path("/{id}")
    public DataResponse<ProductResponse> getProduct(@PathParam("venteId") Long venteId, @PathParam("id") Long id) {
        var product = browseCatalog.getProduct(new ProductId(id));
        var response = ProductRestMapper.toResponse(product);
        return new DataResponse<>(response);
    }
}
