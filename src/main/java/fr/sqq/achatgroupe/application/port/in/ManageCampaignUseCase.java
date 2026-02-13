package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;

import java.util.List;

public interface ManageCampaignUseCase {

    Vente activateCampaign(VenteId venteId);

    Vente deactivateCampaign(VenteId venteId);

    Vente getCampaignStatus(VenteId venteId);

    List<Vente> listAllVentes();
}
