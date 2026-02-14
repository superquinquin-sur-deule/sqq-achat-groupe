package fr.sqq.achatgroupe.infrastructure.in.rest.admin.resource;

import fr.sqq.achatgroupe.application.port.in.GetOrderDetailsUseCase;
import fr.sqq.achatgroupe.application.port.in.ListOrdersUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase;
import fr.sqq.achatgroupe.application.port.in.ManageTimeSlotsUseCase;
import fr.sqq.achatgroupe.application.port.in.MarkOrderPickedUpUseCase;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper.BackofficeOrderRestMapper;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.BackofficeOrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto.BackofficeOrderResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.ProblemDetailResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/api/backoffice/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "backoffice-orders")
public class BackofficeOrderResource {

    private final ListOrdersUseCase listOrders;
    private final GetOrderDetailsUseCase getOrderDetails;
    private final ManageTimeSlotsUseCase manageTimeSlots;
    private final ManageProductsUseCase manageProducts;
    private final MarkOrderPickedUpUseCase markOrderPickedUp;
    private final BackofficeOrderRestMapper mapper;

    public BackofficeOrderResource(
            ListOrdersUseCase listOrders,
            GetOrderDetailsUseCase getOrderDetails,
            ManageTimeSlotsUseCase manageTimeSlots,
            ManageProductsUseCase manageProducts,
            MarkOrderPickedUpUseCase markOrderPickedUp,
            BackofficeOrderRestMapper mapper) {
        this.listOrders = listOrders;
        this.getOrderDetails = getOrderDetails;
        this.manageTimeSlots = manageTimeSlots;
        this.manageProducts = manageProducts;
        this.markOrderPickedUp = markOrderPickedUp;
        this.mapper = mapper;
    }

    @GET
    public DataResponse<List<BackofficeOrderResponse>> listOrders(@QueryParam("venteId") Long venteId) {
        if (venteId == null) {
            throw new BadRequestException("venteId is required");
        }
        List<Order> orders = listOrders.listOrders(venteId);
        Map<Long, TimeSlot> timeSlotsById = manageTimeSlots.listAllTimeSlots(venteId).stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        return new DataResponse<>(mapper.toListResponse(orders, timeSlotsById));
    }

    @PUT
    @Path("/{id}/pickup")
    @APIResponse(responseCode = "204", description = "No content")
    @APIResponse(responseCode = "409", description = "Conflict")
    public Response markAsPickedUp(@PathParam("id") Long id) {
        try {
            markOrderPickedUp.markAsPickedUp(id);
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
    public DataResponse<BackofficeOrderDetailResponse> getOrderDetail(@PathParam("id") Long id) {
        Order order = getOrderDetails.getOrderDetails(id);
        Map<Long, TimeSlot> timeSlotsById = manageTimeSlots.listAllTimeSlots(order.venteId()).stream()
                .collect(Collectors.toMap(TimeSlot::id, Function.identity()));
        Map<Long, Product> productsById = manageProducts.listAllProducts(order.venteId()).stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));
        return new DataResponse<>(mapper.toDetailResponse(order, timeSlotsById, productsById));
    }
}
