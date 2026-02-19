package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.application.port.out.ReceptionRepository;
import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ReceptionEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.ReceptionPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReceptionPanacheRepository implements ReceptionRepository, PanacheRepositoryBase<ReceptionEntity, Long> {

    @Inject
    ReceptionPersistenceMapper mapper;

    @Override
    public Reception save(Reception reception) {
        ReceptionEntity entity = mapper.toEntity(reception);
        entity = getEntityManager().merge(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Reception> findByVenteIdAndSupplier(Long venteId, String supplier) {
        return find("venteId = ?1 and supplier = ?2", venteId, supplier).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<Reception> findAllByVenteId(Long venteId) {
        return find("venteId", venteId).list().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Reception reception) {
        delete("id", reception.id());
    }
}
