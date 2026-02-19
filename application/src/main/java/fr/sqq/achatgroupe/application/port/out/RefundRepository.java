package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.domain.model.payment.Refund;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository {

    Refund save(Refund refund);

    Optional<Refund> findByOrderId(UUID orderId);

    List<Refund> findAllByVenteId(Long venteId);
}
