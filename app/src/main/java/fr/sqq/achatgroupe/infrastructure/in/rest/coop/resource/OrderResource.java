package fr.sqq.achatgroupe.infrastructure.in.rest.coop.resource;

import fr.sqq.achatgroupe.application.command.InitiatePaymentCommand;
import fr.sqq.achatgroupe.application.command.PaymentSession;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.domain.model.payment.PaymentStatus;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.DataResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.InitiatePaymentRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.InitiatePaymentResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.OrderDetailResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.PaymentStatusResponse;
import fr.sqq.mediator.Mediator;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "orders")
public class OrderResource {

    private final Mediator mediator;
    private final OrderRepository orderRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    @ConfigProperty(name = "app.base-url", defaultValue = "http://localhost:8080")
    String appBaseUrl;

    @ConfigProperty(name = "app.order.max-payment-attempts", defaultValue = "2")
    int maxPaymentAttempts;

    public OrderResource(Mediator mediator,
                         OrderRepository orderRepository,
                         TimeSlotRepository timeSlotRepository,
                         ProductRepository productRepository,
                         PaymentRepository paymentRepository) {
        this.mediator = mediator;
        this.orderRepository = orderRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    @GET
    @Path("/{id}")
    public DataResponse<OrderDetailResponse> getOrder(@PathParam("id") Long id) {
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        TimeSlot timeSlot = timeSlotRepository.findSlotById(order.timeSlotId())
                .orElse(null);

        var items = order.items().stream()
                .map(item -> {
                    String productName = productRepository.findById(new ProductId(item.productId()))
                            .map(Product::name)
                            .orElse("Produit #" + item.productId());
                    return new OrderDetailResponse.OrderItemInfo(
                            productName, item.quantity(), item.unitPrice());
                })
                .toList();

        var timeSlotInfo = timeSlot != null
                ? new OrderDetailResponse.TimeSlotInfo(timeSlot.date(), timeSlot.startTime(), timeSlot.endTime())
                : null;

        var response = new OrderDetailResponse(
                order.id(),
                order.orderNumber().value(),
                order.status().name(),
                order.totalAmount(),
                order.customer().name(),
                order.customer().email(),
                timeSlotInfo,
                items,
                order.createdAt()
        );

        return new DataResponse<>(response);
    }

    @GET
    @Path("/{id}/payment-status")
    public DataResponse<PaymentStatusResponse> getPaymentStatus(@PathParam("id") Long id) {
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        Payment payment = paymentRepository.findByOrderId(order.id()).orElse(null);

        int attempts = payment != null ? payment.attempts() : 0;
        String paymentStatus = payment != null ? payment.status().name() : PaymentStatus.PENDING.name();
        boolean canRetry = order.status().name().equals("PENDING")
                && attempts < maxPaymentAttempts;

        var response = new PaymentStatusResponse(
                attempts, maxPaymentAttempts, paymentStatus, order.status().name(), canRetry);
        return new DataResponse<>(response);
    }

    @POST
    @Path("/{id}/payment")
    public DataResponse<InitiatePaymentResponse> initiatePayment(@PathParam("id") Long id, @Valid InitiatePaymentRequest request) {
        validateUrlOrigin(request.successUrl());
        validateUrlOrigin(request.cancelUrl());

        var command = new InitiatePaymentCommand(id, request.successUrl(), request.cancelUrl());
        PaymentSession session = mediator.send(command);
        var response = new InitiatePaymentResponse(session.checkoutUrl());
        return new DataResponse<>(response);
    }

    private void validateUrlOrigin(String url) {
        try {
            // Strip Stripe template placeholders (e.g. {CHECKOUT_SESSION_ID}) before parsing
            String sanitized = url.replaceAll("\\{[^}]+}", "PLACEHOLDER");
            URI uri = URI.create(sanitized);
            URI base = URI.create(appBaseUrl);
            if (!uri.getHost().equals(base.getHost())) {
                throw new BadRequestException("URL non autorisée : domaine invalide");
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("URL invalide");
        }
    }
}
