package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper.OrderRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateOrderRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.OrderResponse;
import fr.sqq.mediator.Mediator;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.RollbackException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/ventes/{venteId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "orders")
public class VenteOrderResource {

    private final Mediator mediator;
    private final OrderRestMapper mapper;

    public VenteOrderResource(Mediator mediator, OrderRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @POST
    @Retry(maxRetries = 2, retryOn = {OptimisticLockException.class, RollbackException.class})
    public RestResponse<DataResponse<OrderResponse>> createOrder(@PathParam("venteId") Long venteId, @Valid CreateOrderRequest request) {
        var command = mapper.toCommand(venteId, request);
        Order order = mediator.send(command);
        OrderResponse response = mapper.toResponse(order);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(response));
    }
}
