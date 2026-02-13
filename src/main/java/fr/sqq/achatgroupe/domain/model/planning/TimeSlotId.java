package fr.sqq.achatgroupe.domain.model.planning;

import java.util.Objects;

public record TimeSlotId(Long value) {

    public TimeSlotId {
        Objects.requireNonNull(value, "TimeSlot id must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("TimeSlot id must be positive");
        }
    }
}
