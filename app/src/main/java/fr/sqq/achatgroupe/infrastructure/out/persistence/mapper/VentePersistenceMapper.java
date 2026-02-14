package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.VenteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface VentePersistenceMapper {

    default Vente toDomain(VenteEntity entity) {
        return new Vente(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                VenteStatus.valueOf(entity.getStatus()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", expression = "java(domain.id())")
    @Mapping(target = "name", expression = "java(domain.name())")
    @Mapping(target = "description", expression = "java(domain.description())")
    @Mapping(target = "status", expression = "java(domain.status().name())")
    @Mapping(target = "startDate", expression = "java(domain.startDate())")
    @Mapping(target = "endDate", expression = "java(domain.endDate())")
    @Mapping(target = "createdAt", expression = "java(domain.createdAt())")
    VenteEntity toEntity(Vente domain);
}
