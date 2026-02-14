package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.coop.dto.VenteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface VenteRestMapper {

    @Mapping(target = "id", expression = "java(vente.id())")
    @Mapping(target = "name", expression = "java(vente.name())")
    @Mapping(target = "description", expression = "java(vente.description())")
    @Mapping(target = "status", expression = "java(vente.status().name())")
    @Mapping(target = "startDate", expression = "java(vente.startDate())")
    @Mapping(target = "endDate", expression = "java(vente.endDate())")
    @Mapping(target = "createdAt", expression = "java(vente.createdAt())")
    VenteResponse toResponse(Vente vente);
}
