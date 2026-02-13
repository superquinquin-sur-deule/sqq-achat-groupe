package fr.sqq.achatgroupe.domain.model.vente;

import java.time.Instant;

public class Vente {

    private final Long id;
    private final String name;
    private final String description;
    private final VenteStatus status;
    private final Instant createdAt;

    public Vente(Long id, String name, String description, VenteStatus status, Instant createdAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vente name must not be blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("Vente status must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Vente createdAt must not be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Vente create(String name, String description) {
        return new Vente(null, name, description, VenteStatus.ACTIVE, Instant.now());
    }

    public Vente activate() {
        if (this.status == VenteStatus.ACTIVE) {
            return this;
        }
        return new Vente(id, name, description, VenteStatus.ACTIVE, createdAt);
    }

    public Vente close() {
        if (this.status == VenteStatus.CLOSED) {
            return this;
        }
        return new Vente(id, name, description, VenteStatus.CLOSED, createdAt);
    }

    public Long id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public VenteStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
}
