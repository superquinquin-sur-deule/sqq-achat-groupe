package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;
import java.util.UUID;

public record GeneratePreparationListQuery(Long venteId) implements Query<List<GeneratePreparationListQuery.PreparationOrder>> {

    public record PreparationOrder(UUID orderId, String orderNumber, String customerFirstName, String customerLastName,
                                   String customerEmail, String customerPhone, String timeSlotLabel,
                                   List<PreparationItem> items) {
    }

    public record PreparationItem(String productName, int quantity) {
    }
}
