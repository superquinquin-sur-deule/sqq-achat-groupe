package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.util.List;

public record ShortagePreviewResponse(List<ShortageItem> items) {

    public record ShortageItem(Long productId, String productName, String supplier,
                               int orderedQty, int receivedQty, int shortage, int affectedOrderCount) {}
}
