package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.payment.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByOrderIdForUpdate(Long orderId);

    Optional<Payment> findByStripePaymentId(String stripePaymentId);
}
