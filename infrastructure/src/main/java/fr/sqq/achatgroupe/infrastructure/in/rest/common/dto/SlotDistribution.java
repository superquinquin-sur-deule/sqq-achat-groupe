package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.time.LocalDate;

public record SlotDistribution(
        Long slotId,
        String slotLabel,
        long orderCount,
        LocalDate date
) {
}
