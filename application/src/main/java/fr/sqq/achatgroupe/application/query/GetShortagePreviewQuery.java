package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;

public record GetShortagePreviewQuery(Long venteId) implements Query<GetShortagePreviewQuery.ShortagePreviewResult> {

    public record ShortagePreviewResult(List<ShortageItem> items) {}

    public record ShortageItem(Long productId, String productName, String supplier,
                               int orderedQty, int receivedQty, int shortage, int affectedOrderCount) {}
}
