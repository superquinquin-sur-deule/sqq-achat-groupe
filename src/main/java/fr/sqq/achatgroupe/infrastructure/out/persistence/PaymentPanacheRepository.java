package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.PaymentEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.PaymentPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@ApplicationScoped
public class PaymentPanacheRepository implements PaymentRepository, PanacheRepositoryBase<PaymentEntity, Long> {

    @Inject
    PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = mapper.toEntity(payment);
        entity = getEntityManager().merge(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return find("orderId", orderId).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderIdForUpdate(Long orderId) {
        return find("orderId", orderId).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByStripePaymentId(String stripePaymentId) {
        return find("stripePaymentId", stripePaymentId).firstResultOptional()
                .map(mapper::toDomain);
    }
}
