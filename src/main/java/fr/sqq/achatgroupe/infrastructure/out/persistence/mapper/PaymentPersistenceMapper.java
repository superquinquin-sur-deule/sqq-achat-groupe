package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.domain.model.payment.PaymentStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.PaymentEntity;

public class PaymentPersistenceMapper {

    private PaymentPersistenceMapper() {
    }

    public static Payment toDomain(PaymentEntity entity) {
        return new Payment(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getStripePaymentId(),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getAttempts(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    public static PaymentEntity toEntity(Payment domain) {
        var entity = new PaymentEntity();
        entity.setId(domain.id());
        entity.setOrderId(domain.orderId());
        entity.setAmount(domain.amount());
        entity.setStripePaymentId(domain.stripePaymentId());
        entity.setStatus(domain.status().name());
        entity.setAttempts(domain.attempts());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        entity.setVersion(domain.version());
        return entity;
    }
}
