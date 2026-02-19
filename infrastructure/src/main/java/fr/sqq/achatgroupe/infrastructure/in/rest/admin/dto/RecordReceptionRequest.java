package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.util.List;

public record RecordReceptionRequest(String supplier, List<ReceptionLineRequest> lines) {
    public record ReceptionLineRequest(Long productId, int receivedQuantity) {}
}
