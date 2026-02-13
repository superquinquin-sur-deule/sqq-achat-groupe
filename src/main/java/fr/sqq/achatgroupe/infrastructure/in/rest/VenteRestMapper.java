package fr.sqq.achatgroupe.infrastructure.in.rest;

import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase.CreateVenteCommand;
import fr.sqq.achatgroupe.application.port.in.CreateVenteUseCase.CreateVenteResult;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateVenteRequest;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CreateVenteResponse;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.VenteResponse;

public class VenteRestMapper {

    private VenteRestMapper() {
    }

    public static VenteResponse toResponse(Vente vente) {
        return new VenteResponse(
                vente.id(),
                vente.name(),
                vente.description(),
                vente.status().name(),
                vente.createdAt()
        );
    }

    public static CreateVenteResponse toCreateResponse(CreateVenteResult result) {
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

    public static CreateVenteCommand toCommand(CreateVenteRequest request) {
        var products = request.products().stream()
                .map(p -> new CreateVenteCommand.ProductCommand(p.name(), p.description(), p.price(), p.supplier(), p.stock()))
                .toList();
        var timeSlots = request.timeSlots().stream()
                .map(t -> new CreateVenteCommand.TimeSlotCommand(t.date(), t.startTime(), t.endTime(), t.capacity()))
                .toList();
        return new CreateVenteCommand(request.name(), request.description(), products, timeSlots);
    }
}
