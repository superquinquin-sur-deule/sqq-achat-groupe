package fr.sqq.achatgroupe.domain.model.order;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public record OrderNumber(String value) {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order number must not be blank");
        }
    }

    public static OrderNumber generate() {
        int year = LocalDate.now().getYear();
        long timestamp = System.currentTimeMillis() % 100_000;
        int seq = COUNTER.incrementAndGet() % 10_000;
        String unique = String.format("%05d%04d", timestamp, seq);
        return new OrderNumber("AG-" + year + "-" + unique);
    }
}
