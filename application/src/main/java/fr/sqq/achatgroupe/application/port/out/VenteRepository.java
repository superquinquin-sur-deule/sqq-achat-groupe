package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;

import java.util.List;
import java.util.Optional;

public interface VenteRepository {

    Vente save(Vente vente);

    Optional<Vente> findById(VenteId id);

    List<Vente> findAllActive();

    CursorPage<Vente> findAllActive(CursorPageRequest pageRequest);

    List<Vente> findAllVentes();

    CursorPage<Vente> findAllVentes(CursorPageRequest pageRequest);

    void deleteById(VenteId id);
}
