package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.domain.exception.MaxPaymentAttemptsExceededException;
import fr.sqq.achatgroupe.domain.exception.OrderNotFoundException;
import fr.sqq.achatgroupe.domain.exception.PaymentAlreadySucceededException;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderStatus;
import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.application.port.in.InitiatePaymentUseCase;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class InitiatePayment implements InitiatePaymentUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final int maxPaymentAttempts;

    public InitiatePayment(OrderRepository orderRepository, PaymentRepository paymentRepository,
                           PaymentGateway paymentGateway,
                           @ConfigProperty(name = "app.order.max-payment-attempts", defaultValue = "2") int maxPaymentAttempts) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.maxPaymentAttempts = maxPaymentAttempts;
    }

    @Override
    @Transactional
    public PaymentSession execute(InitiatePaymentCommand command) {
        Order order = orderRepository.findOrderById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new PaymentAlreadySucceededException(command.orderId());
        }

        Payment payment = paymentRepository.findByOrderIdForUpdate(order.id())
                .orElseGet(() -> Payment.create(order.id(), order.totalAmount()));

        if (payment.isAlreadySucceeded()) {
            throw new PaymentAlreadySucceededException(order.id());
        }

        if (payment.attempts() >= maxPaymentAttempts) {
            throw new MaxPaymentAttemptsExceededException(order.id(), maxPaymentAttempts);
        }

        payment.recordAttempt();

        PaymentGateway.PaymentSessionResult result = paymentGateway.createCheckoutSession(
                order, command.successUrl(), command.cancelUrl());

        paymentRepository.save(payment);

        return new PaymentSession(result.checkoutUrl(), result.sessionId());
    }
}
