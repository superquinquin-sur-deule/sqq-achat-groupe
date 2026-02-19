package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.reception.Reception;

import java.util.List;
import java.util.Optional;

public interface ReceptionRepository {

    Reception save(Reception reception);

    Optional<Reception> findByVenteIdAndSupplier(Long venteId, String supplier);

    List<Reception> findAllByVenteId(Long venteId);

    void delete(Reception reception);
}
