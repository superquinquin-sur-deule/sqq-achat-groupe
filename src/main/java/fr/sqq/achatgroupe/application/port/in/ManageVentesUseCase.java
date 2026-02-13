package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;

import java.time.Instant;
import java.util.List;

public interface ManageVentesUseCase {

    List<Vente> listAllVentes();

    Vente getVente(VenteId id);

    Vente createVente(CreateCommand cmd);

    Vente updateVente(UpdateCommand cmd);

    void deleteVente(VenteId id);

    Vente activateVente(VenteId id);

    Vente deactivateVente(VenteId id);

    record CreateCommand(String name, String description, Instant startDate, Instant endDate) {}

    record UpdateCommand(VenteId id, String name, String description, Instant startDate, Instant endDate) {}
}
