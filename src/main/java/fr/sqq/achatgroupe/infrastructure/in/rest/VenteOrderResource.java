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
import jakarta.ws.rs.core.Response;

@Path("/api/ventes/{venteId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenteOrderResource {

    private final CreateOrderUseCase createOrderUseCase;

    public VenteOrderResource(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @POST
    public Response createOrder(@PathParam("venteId") Long venteId, @Valid CreateOrderRequest request) {
        var command = OrderRestMapper.toCommand(venteId, request);
        Order order = createOrderUseCase.execute(command);
        OrderResponse response = OrderRestMapper.toResponse(order);
        return Response.status(Response.Status.CREATED)
                .entity(new DataResponse<>(response))
                .build();
    }
}
