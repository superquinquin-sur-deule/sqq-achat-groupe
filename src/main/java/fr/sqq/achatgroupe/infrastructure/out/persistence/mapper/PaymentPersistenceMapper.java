package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.domain.model.payment.PaymentStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface PaymentPersistenceMapper {

    default Payment toDomain(PaymentEntity entity) {
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

    @Mapping(target = "id", expression = "java(domain.id())")
    @Mapping(target = "orderId", expression = "java(domain.orderId())")
    @Mapping(target = "amount", expression = "java(domain.amount())")
    @Mapping(target = "stripePaymentId", expression = "java(domain.stripePaymentId())")
    @Mapping(target = "status", expression = "java(domain.status().name())")
    @Mapping(target = "attempts", expression = "java(domain.attempts())")
    @Mapping(target = "createdAt", expression = "java(domain.createdAt())")
    @Mapping(target = "updatedAt", expression = "java(domain.updatedAt())")
    @Mapping(target = "version", expression = "java(domain.version())")
    PaymentEntity toEntity(Payment domain);
}
