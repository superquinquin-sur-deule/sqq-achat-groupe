package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.payment.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByOrderIdForUpdate(UUID orderId);

    Optional<Payment> findByStripePaymentId(String stripePaymentId);
}
