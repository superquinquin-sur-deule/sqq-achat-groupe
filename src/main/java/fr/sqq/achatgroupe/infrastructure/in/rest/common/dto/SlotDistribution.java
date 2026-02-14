package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

public record SlotDistribution(
        Long slotId,
        String slotLabel,
        long orderCount
) {
}
