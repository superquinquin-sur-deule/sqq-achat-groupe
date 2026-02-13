package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GetOrderDetailsUseCase;
import fr.sqq.achatgroupe.application.port.in.ListOrdersUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.application.port.in.MarkOrderPickedUpUseCase;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.BackofficeOrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.BackofficeOrderResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.ProblemDetailResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/api/backoffice/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BackofficeOrderResource {

    private final ListOrdersUseCase listOrders;
    private final GetOrderDetailsUseCase getOrderDetails;
    private final ManageTimeSlotsUseCase manageTimeSlots;
    private final ManageProductsUseCase manageProducts;
    private final MarkOrderPickedUpUseCase markOrderPickedUp;

    public BackofficeOrderResource(
            ListOrdersUseCase listOrders,
            GetOrderDetailsUseCase getOrderDetails,
            ManageTimeSlotsUseCase manageTimeSlots,
            ManageProductsUseCase manageProducts,
            MarkOrderPickedUpUseCase markOrderPickedUp) {
        this.listOrders = listOrders;
        this.getOrderDetails = getOrderDetails;
        this.manageTimeSlots = manageTimeSlots;
        this.manageProducts = manageProducts;
        this.markOrderPickedUp = markOrderPickedUp;
    }

    @GET
    public DataResponse<List<BackofficeOrderResponse>> listOrders(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<Order> orders = listOrders.listOrders(venteId);
        Map<Long, TimeSlot> timeSlotsById = manageTimeSlots.listAllTimeSlots(venteId).stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        return new DataResponse<>(BackofficeOrderRestMapper.toListResponse(orders, timeSlotsById));
    }

    @PUT
    @Path("/{id}/pickup")
    public Response markAsPickedUp(@PathParam("id") Long id) {
        try {
            markOrderPickedUp.markAsPickedUp(id);
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.valueOf("application/problem+json"))
                    .entity(new ProblemDetailResponse("about:blank", "Transition invalide", 409, e.getMessage()))
                    .build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public DataResponse<BackofficeOrderDetailResponse> getOrderDetail(@PathParam("id") Long id) {
        Order order = getOrderDetails.getOrderDetails(id);
        Map<Long, TimeSlot> timeSlotsById = manageTimeSlots.listAllTimeSlots(order.venteId()).stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        Map<Long, Product> productsById = manageProducts.listAllProducts(order.venteId()).stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));
        return new DataResponse<>(BackofficeOrderRestMapper.toDetailResponse(order, timeSlotsById, productsById));
    }
}
