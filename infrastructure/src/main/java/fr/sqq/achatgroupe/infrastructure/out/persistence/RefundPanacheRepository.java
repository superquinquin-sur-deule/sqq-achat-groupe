package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.application.port.out.RefundRepository;
import fr.sqq.achatgroupe.domain.model.payment.Refund;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.RefundEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.RefundPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefundPanacheRepository implements RefundRepository, PanacheRepositoryBase<RefundEntity, Long> {

    @Inject
    RefundPersistenceMapper mapper;

    @Override
    public Refund save(Refund refund) {
        RefundEntity entity = mapper.toEntity(refund);
        entity = getEntityManager().merge(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Refund> findByOrderId(UUID orderId) {
        return find("orderId", orderId).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<Refund> findAllByVenteId(Long venteId) {
        return find("orderId in (select o.id from OrderEntity o where o.venteId = ?1)", venteId)
                .list().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
