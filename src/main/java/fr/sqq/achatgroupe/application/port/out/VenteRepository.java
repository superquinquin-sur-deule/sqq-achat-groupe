package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;

import java.util.List;
import java.util.Optional;

public interface VenteRepository {

    Vente save(Vente vente);

    Optional<Vente> findById(VenteId id);

    List<Vente> findAllActive();

    List<Vente> findAllVentes();
}
