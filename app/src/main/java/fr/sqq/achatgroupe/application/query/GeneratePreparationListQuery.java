package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;

public record GeneratePreparationListQuery(Long venteId) implements Query<List<GeneratePreparationListQuery.PreparationOrder>> {

    public record PreparationOrder(Long orderId, String orderNumber, String customerName, String customerEmail,
                                   String customerPhone, String timeSlotLabel, List<PreparationItem> items) {
    }

    public record PreparationItem(String productName, int quantity) {
    }
}
