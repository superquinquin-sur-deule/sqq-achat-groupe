package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.OrderDetailResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "orders")
public class OrderResource {

    private final OrderRepository orderRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ProductRepository productRepository;
    private final VenteRepository venteRepository;

    public OrderResource(OrderRepository orderRepository,
                         TimeSlotRepository timeSlotRepository,
                         ProductRepository productRepository,
                         VenteRepository venteRepository) {
        this.orderRepository = orderRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.productRepository = productRepository;
        this.venteRepository = venteRepository;
    }

    @GET
    @Path("/{id}")
    public DataResponse<OrderDetailResponse> getOrder(@PathParam("id") UUID id) {
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        TimeSlot timeSlot = order.timeSlotId() != null
                ? timeSlotRepository.findSlotById(order.timeSlotId()).orElse(null)
                : null;

        Vente vente = venteRepository.findById(new VenteId(order.venteId())).orElse(null);

        var items = order.items().stream()
                .map(item -> {
                    String productName = productRepository.findById(new ProductId(item.productId()))
                            .map(Product::name)
                            .orElse("Produit #" + item.productId());
                    return new OrderDetailResponse.OrderItemInfo(
                            productName, item.quantity(), item.unitPrice().amount());
                })
                .toList();

        var timeSlotInfo = timeSlot != null
                ? new OrderDetailResponse.TimeSlotInfo(timeSlot.date(), timeSlot.startTime(), timeSlot.endTime())
                : null;

        var response = new OrderDetailResponse(
                order.id(),
                order.orderNumber().value(),
                order.status().name(),
                order.totalAmount().amount(),
                order.customer().firstName(),
                order.customer().lastName(),
                order.customer().email(),
                timeSlotInfo,
                vente != null ? vente.pickupMode().name() : null,
                vente != null ? vente.pickupStartDate() : null,
                vente != null ? vente.pickupEndDate() : null,
                items,
                order.createdAt()
        );

        return new DataResponse<>(response);
    }
}
