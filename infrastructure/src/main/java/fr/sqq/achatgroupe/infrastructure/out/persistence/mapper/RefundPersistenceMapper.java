package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.payment.Refund;
import fr.sqq.achatgroupe.domain.model.payment.RefundStatus;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.RefundEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface RefundPersistenceMapper {

    default Refund toDomain(RefundEntity entity) {
        return new Refund(
                entity.getId(),
                entity.getOrderId(),
                Money.eur(entity.getAmount()),
                entity.getStripeRefundId(),
                RefundStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default RefundEntity toEntity(Refund domain) {
        var entity = new RefundEntity();
        entity.setId(domain.id());
        entity.setOrderId(domain.orderId());
        entity.setAmount(domain.amount().amount());
        entity.setStripeRefundId(domain.stripeRefundId());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        return entity;
    }
}
