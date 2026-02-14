package fr.sqq.achatgroupe.infrastructure.in.rest.coop.mapper;

import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase.CreateVenteCommand;
import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase.CreateVenteResult;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.CreateVenteResponse;
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

    default CreateVenteResponse toCreateResponse(CreateVenteResult result) {
        Vente vente = result.vente();
        return new CreateVenteResponse(
                vente.id(),
                vente.name(),
                vente.description(),
                vente.status().name(),
                vente.createdAt(),
                result.productIds(),
                result.timeSlotIds()
        );
    }

    default CreateVenteCommand toCommand(CreateVenteRequest request) {
        var products = request.products().stream()
                .map(p -> new CreateVenteCommand.ProductCommand(p.name(), p.description(), p.price(), p.supplier(), p.stock()))
                .toList();
        var timeSlots = request.timeSlots().stream()
                .map(t -> new CreateVenteCommand.TimeSlotCommand(t.date(), t.startTime(), t.endTime(), t.capacity()))
                .toList();
        return new CreateVenteCommand(request.name(), request.description(), products, timeSlots);
    }
}
