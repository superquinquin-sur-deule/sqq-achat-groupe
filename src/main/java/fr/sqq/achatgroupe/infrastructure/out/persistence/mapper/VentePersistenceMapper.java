package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.VenteEntity;

public class VentePersistenceMapper {

    private VentePersistenceMapper() {
    }

    public static Vente toDomain(VenteEntity entity) {
        return new Vente(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                VenteStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    public static VenteEntity toEntity(Vente domain) {
        var entity = new VenteEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
