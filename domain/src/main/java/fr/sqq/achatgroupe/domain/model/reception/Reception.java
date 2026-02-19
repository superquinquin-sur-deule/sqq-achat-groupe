package fr.sqq.achatgroupe.domain.model.reception;

import java.time.Instant;
import java.util.List;

public class Reception {

    private final Long id;
    private final Long venteId;
    private final String supplier;
    private final List<ReceptionItem> items;
    private final ReceptionStatus status;
    private final Instant createdAt;

    public Reception(Long id, Long venteId, String supplier, List<ReceptionItem> items,
                     ReceptionStatus status, Instant createdAt) {
        if (venteId == null) throw new IllegalArgumentException("Vente ID must not be null");
        if (supplier == null || supplier.isBlank()) throw new IllegalArgumentException("Supplier must not be blank");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Items must not be empty");
        this.id = id;
        this.venteId = venteId;
        this.supplier = supplier;
        this.items = List.copyOf(items);
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Reception create(Long venteId, String supplier, List<ReceptionItem> items) {
        return new Reception(null, venteId, supplier, items, ReceptionStatus.COMPLETED, Instant.now());
    }

    public Long id() { return id; }
    public Long venteId() { return venteId; }
    public String supplier() { return supplier; }
    public List<ReceptionItem> items() { return items; }
    public ReceptionStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
}
