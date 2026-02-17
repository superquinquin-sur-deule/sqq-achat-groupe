package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;
import java.util.UUID;

public record GenerateDistributionListQuery(Long venteId) implements Query<List<GenerateDistributionListQuery.DistributionOrder>> {

    public record DistributionOrder(UUID orderId, String orderNumber, String customerFirstName, String customerLastName,
                                    String timeSlotLabel) {
    }
}
