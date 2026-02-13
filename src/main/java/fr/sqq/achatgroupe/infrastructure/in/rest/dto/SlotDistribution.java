package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

public record SlotDistribution(
        Long slotId,
        String slotLabel,
        long orderCount
) {
}
