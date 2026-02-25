package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.port.out.ProductImageRepository;
import fr.sqq.achatgroupe.application.port.out.ProductImageRepository.ProductImageData;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/product-images")
@Tag(name = "product-images")
public class ProductImageResource {

    private final ProductImageRepository productImageRepository;

    public ProductImageResource(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @GET
    @Path("/{productId}")
    public Response getImage(@PathParam("productId") Long productId) {
        return productImageRepository.findByProductId(productId)
                .map(this::buildImageResponse)
                .orElseGet(() -> Response.status(404).build());
    }

    private Response buildImageResponse(ProductImageData imageData) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(86400);
        cacheControl.setPrivate(false);

        return Response.ok(imageData.data())
                .type(imageData.contentType())
                .cacheControl(cacheControl)
                .build();
    }
}
