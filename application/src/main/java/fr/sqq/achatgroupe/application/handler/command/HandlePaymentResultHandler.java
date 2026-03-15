package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.command.HandlePaymentResultCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.PaymentWebhookResult;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.PaymentWebhookStatus;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.exception.PaymentWebhookException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import fr.sqq.mediator.Mediator;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class HandlePaymentResultHandler implements CommandHandler<HandlePaymentResultCommand, Void> {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final Mediator mediator;
    private final int maxPaymentAttempts;

    public HandlePaymentResultHandler(PaymentGateway paymentGateway, PaymentRepository paymentRepository,
                                      OrderRepository orderRepository, ProductRepository productRepository,
                                      TimeSlotRepository timeSlotRepository, Mediator mediator,
                                      @ConfigProperty(name = "app.order.max-payment-attempts", defaultValue = "2") int maxPaymentAttempts) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.mediator = mediator;
        this.maxPaymentAttempts = maxPaymentAttempts;
    }

    @Override
    @Transactional
    public Void handle(HandlePaymentResultCommand command) {
        PaymentWebhookResult result = paymentGateway.parseWebhookEvent(command.payload(), command.signature());

        if (result.status() == PaymentWebhookStatus.IGNORED) {
            Log.infof("Webhook ignoré : événement non pertinent");
            return null;
        }

        Order order = orderRepository.findOrderById(result.orderId())
                .orElseThrow(() -> new OrderNotFoundException(result.orderId()));

        Payment payment = paymentRepository.findByOrderIdForUpdate(order.id())
                .orElseThrow(() -> new PaymentWebhookException(
                        "Paiement introuvable pour la commande " + order.id()));

        if (payment.isAlreadySucceeded()) {
            Log.infof("Webhook ignoré : paiement déjà réussi pour la commande %s (stripe_payment_id=%s)",
                    order.id(), result.externalPaymentId());
            return null;
        }

        if (result.status() == PaymentWebhookStatus.SUCCEEDED) {
            payment.markAsSucceeded(result.externalPaymentId());
            if (order.status() == OrderStatus.CANCELLED) {
                for (OrderItem item : order.items()) {
                    Product product = productRepository.findById(new ProductId(item.productId()))
                            .orElseThrow(() -> new ProductNotFoundException(new ProductId(item.productId())));
                    product.decrementStock(item.quantity());
                    productRepository.save(product);
                }
                if (order.timeSlotId() != null) {
                    TimeSlot slot = timeSlotRepository.findSlotById(order.timeSlotId())
                            .orElseThrow(() -> new TimeSlotNotFoundException(order.timeSlotId()));
                    slot.reserveOnePlace();
                    timeSlotRepository.save(slot);
                }
                order.reactivateAfterPayment();
                Log.warnf("Commande %s réactivée après paiement — stocks re-décrémentés, créneau re-réservé (stripe_payment_id=%s)",
                        order.id(), result.externalPaymentId());
            } else {
                order.markAsPaid();
                Log.infof("Paiement réussi pour la commande %s (stripe_payment_id=%s)",
                        order.id(), result.externalPaymentId());
            }
            orderRepository.save(order);
            paymentRepository.save(payment);
        } else if (result.status() == PaymentWebhookStatus.FAILED) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            Log.warnf("Paiement échoué pour la commande %s (tentatives: %d/%d)",
                    order.id(), payment.attempts(), maxPaymentAttempts);

            if (payment.attempts() >= maxPaymentAttempts) {
                mediator.send(new CancelOrderCommand(order.id()));
                Log.infof("Commande %s annulée après %d tentatives de paiement échouées",
                        order.id(), maxPaymentAttempts);
            }
        }

        return null;
    }
}
