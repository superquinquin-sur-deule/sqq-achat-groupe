package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;

public record GetReceptionStatusQuery(Long venteId) implements Query<GetReceptionStatusQuery.ReceptionStatusResult> {

    public record ReceptionStatusResult(List<SupplierReceptionStatus> suppliers, boolean allReceived, boolean hasRefunds) {}

    public record SupplierReceptionStatus(String supplier, Long receptionId, String status,
                                          List<ReceptionLineStatus> lines) {}

    public record ReceptionLineStatus(Long productId, String productName, int orderedQuantity,
                                      int receivedQuantity, int shortage) {}
}
