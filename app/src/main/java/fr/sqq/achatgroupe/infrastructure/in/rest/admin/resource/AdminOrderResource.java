package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.command.MarkOrderPickedUpCommand;
import fr.sqq.achatgroupe.application.query.GetOrderDetailsQuery;
import fr.sqq.achatgroupe.application.query.ListAllProductsQuery;
import fr.sqq.achatgroupe.application.query.ListAllTimeSlotsQuery;
import fr.sqq.achatgroupe.application.query.ListOrdersQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.AdminOrderRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminOrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.AdminOrderResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProblemDetailResponse;
import fr.sqq.mediator.Mediator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/api/admin/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "admin-orders")
public class AdminOrderResource {

    private final Mediator mediator;
    private final AdminOrderRestMapper mapper;

    public AdminOrderResource(Mediator mediator, AdminOrderRestMapper mapper) {
        this.mediator = mediator;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<AdminOrderResponse>> listOrders(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<Order> orders = mediator.send(new ListOrdersQuery(venteId));
        List<TimeSlot> timeSlots = mediator.send(new ListAllTimeSlotsQuery(venteId));
        Map<Long, TimeSlot> timeSlotsById = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        return new DataResponse<>(mapper.toListResponse(orders, timeSlotsById));
    }

    @PUT
    @Path("/{id}/pickup")
    @APIResponse(responseCode = "204", description = "No content")
    @APIResponse(responseCode = "409", description = "Conflict")
    public Response markAsPickedUp(@PathParam("id") Long id) {
        try {
            mediator.send(new MarkOrderPickedUpCommand(id));
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.valueOf("application/problem+json"))
                    .entity(new ProblemDetailResponse("about:blank", "Transition invalide", 409, e.getMessage()))
                    .build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public DataResponse<AdminOrderDetailResponse> getOrderDetail(@PathParam("id") Long id) {
        Order order = mediator.send(new GetOrderDetailsQuery(id));
        List<TimeSlot> timeSlots = mediator.send(new ListAllTimeSlotsQuery(order.venteId()));
        Map<Long, TimeSlot> timeSlotsById = timeSlots.stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        List<Product> products = mediator.send(new ListAllProductsQuery(order.venteId()));
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));
        return new DataResponse<>(mapper.toDetailResponse(order, timeSlotsById, productsById));
    }
}
