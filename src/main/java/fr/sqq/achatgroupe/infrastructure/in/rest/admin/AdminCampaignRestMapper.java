package fr.sqq.achatgroupe.infrastructure.in.rest.admin;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.CampaignResponse;

public class AdminCampaignRestMapper {

    private AdminCampaignRestMapper() {
    }

    public static CampaignResponse toResponse(Vente vente) {
        return new CampaignResponse(
                vente.id(),
                vente.name(),
                vente.status().name()
        );
    }
}
