package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.command.HandlePaymentResultCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.PaymentWebhookResult;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.PaymentWebhookStatus;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.exception.PaymentWebhookException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.payment.Payment;
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
    private final Mediator mediator;
    private final int maxPaymentAttempts;

    public HandlePaymentResultHandler(PaymentGateway paymentGateway, PaymentRepository paymentRepository,
                                      OrderRepository orderRepository, Mediator mediator,
                                      @ConfigProperty(name = "app.order.max-payment-attempts", defaultValue = "2") int maxPaymentAttempts) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
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

        Payment payment = paymentRepository.findByOrderId(order.id())
                .orElseThrow(() -> new PaymentWebhookException(
                        "Paiement introuvable pour la commande " + order.id()));

        if (payment.isAlreadySucceeded()) {
            Log.infof("Webhook ignoré : paiement déjà réussi pour la commande %d (stripe_payment_id=%s)",
                    order.id(), result.externalPaymentId());
            return null;
        }

        if (result.status() == PaymentWebhookStatus.SUCCEEDED) {
            payment.markAsSucceeded(result.externalPaymentId());
            order.markAsPaid();
            orderRepository.save(order);
            paymentRepository.save(payment);
            Log.infof("Paiement réussi pour la commande %d (stripe_payment_id=%s)",
                    order.id(), result.externalPaymentId());
        } else if (result.status() == PaymentWebhookStatus.FAILED) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            Log.warnf("Paiement échoué pour la commande %d (tentatives: %d/%d)",
                    order.id(), payment.attempts(), maxPaymentAttempts);

            if (payment.attempts() >= maxPaymentAttempts) {
                mediator.send(new CancelOrderCommand(order.id()));
                Log.infof("Commande %d annulée après %d tentatives de paiement échouées",
                        order.id(), maxPaymentAttempts);
            }
        }

        return null;
    }
}
