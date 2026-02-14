package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.query.GetProductQuery;
import fr.sqq.achatgroupe.application.query.ListActiveProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.ProductRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductListResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductResponse;
import fr.sqq.mediator.Mediator;
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

    private final Mediator mediator;
    private final ProductRestMapper mapper;

    public ProductResource(Mediator mediator, ProductRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public ProductListResponse listProducts(@PathParam("venteId") Long venteId) {
        List<Product> products = mediator.send(new ListActiveProductsQuery(venteId));
        List<ProductResponse> responses = products.stream()
                .map(mapper::toResponse)
                .toList();

        var meta = new ProductListResponse.Meta(responses.size(), 1, responses.size());
        return new ProductListResponse(responses, meta);
    }

    @GET
    @Path("/{id}")
    public DataResponse<ProductResponse> getProduct(@PathParam("venteId") Long venteId, @PathParam("id") Long id) {
        Product product = mediator.send(new GetProductQuery(new ProductId(id)));
        var response = mapper.toResponse(product);
        return new DataResponse<>(response);
    }
}
