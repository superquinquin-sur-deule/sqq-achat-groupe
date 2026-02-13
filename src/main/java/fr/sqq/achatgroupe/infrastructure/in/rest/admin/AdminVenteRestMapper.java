package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.AdminVenteResponse;

public class AdminVenteRestMapper {

    private AdminVenteRestMapper() {
    }

    public static AdminVenteResponse toResponse(Vente vente) {
        return new AdminVenteResponse(
                vente.id(),
                vente.name(),
                vente.description(),
                vente.status().name(),
                vente.startDate(),
                vente.endDate(),
                vente.createdAt()
        );
    }
}
