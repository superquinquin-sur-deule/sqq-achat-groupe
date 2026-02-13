package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.VenteEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.VentePersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VentePanacheRepository implements VenteRepository, PanacheRepositoryBase<VenteEntity, Long> {

    @Override
    public Vente save(Vente vente) {
        VenteEntity entity = VentePersistenceMapper.toEntity(vente);
        entity = getEntityManager().merge(entity);
        getEntityManager().flush();
        return VentePersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Vente> findById(VenteId id) {
        return find("id", id.value()).firstResultOptional()
                .map(VentePersistenceMapper::toDomain);
    }

    @Override
    public List<Vente> findAllActive() {
        return list("status = 'ACTIVE' AND (startDate IS NULL OR startDate <= CURRENT_TIMESTAMP) AND (endDate IS NULL OR endDate >= CURRENT_TIMESTAMP)").stream()
                .map(VentePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Vente> findAllVentes() {
        return listAll(io.quarkus.panache.common.Sort.descending("id")).stream()
                .map(VentePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(VenteId id) {
        delete("id", id.value());
    }
}
