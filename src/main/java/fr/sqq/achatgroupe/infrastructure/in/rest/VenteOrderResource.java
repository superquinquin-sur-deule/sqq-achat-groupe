package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateOrderRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.OrderResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/ventes/{venteId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "orders")
public class VenteOrderResource {

    private final CreateOrderUseCase createOrderUseCase;

    public VenteOrderResource(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @POST
    public RestResponse<DataResponse<OrderResponse>> createOrder(@PathParam("venteId") Long venteId, @Valid CreateOrderRequest request) {
        var command = OrderRestMapper.toCommand(venteId, request);
        Order order = createOrderUseCase.execute(command);
        OrderResponse response = OrderRestMapper.toResponse(order);
        return RestResponse.status(RestResponse.Status.CREATED, new DataResponse<>(response));
    }
}
