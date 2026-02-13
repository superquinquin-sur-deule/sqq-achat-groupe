package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;

public interface GetVenteUseCase {

    Vente getVente(VenteId id);
}
