package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.util.List;

public record ReceptionStatusResponse(List<SupplierReceptionStatus> suppliers, boolean allReceived, boolean hasRefunds) {

    public record SupplierReceptionStatus(String supplier, Long receptionId, String status,
                                          List<ReceptionLineResponse> lines) {}

    public record ReceptionLineResponse(Long productId, String productName, int orderedQuantity,
                                        int receivedQuantity, int shortage) {}
}
