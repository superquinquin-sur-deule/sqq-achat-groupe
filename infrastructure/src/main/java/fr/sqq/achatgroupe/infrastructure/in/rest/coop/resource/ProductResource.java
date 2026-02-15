package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.application.query.GetProductQuery;
import fr.sqq.achatgroupe.application.query.ListActiveProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.ProductRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CursorPageResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProductResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
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
    public CursorPageResponse<ProductResponse> listProducts(
            @PathParam("venteId") Long venteId,
            @QueryParam("cursor") String cursor,
            @QueryParam("size") @DefaultValue("50") int size) {
        var pageRequest = cursor != null ? CursorPageRequest.after(cursor, size) : CursorPageRequest.first(size);
        CursorPage<Product> page = mediator.send(new ListActiveProductsQuery(venteId, pageRequest));
        List<ProductResponse> responses = page.items().stream()
                .map(mapper::toResponse)
                .toList();
        return new CursorPageResponse<>(responses, new CursorPageResponse.PageInfo(page.endCursor(), page.hasNext()));
    }

    @GET
    @Path("/{id}")
    public DataResponse<ProductResponse> getProduct(@PathParam("venteId") Long venteId, @PathParam("id") Long id) {
        Product product = mediator.send(new GetProductQuery(new ProductId(id)));
        var response = mapper.toResponse(product);
        return new DataResponse<>(response);
    }
}
