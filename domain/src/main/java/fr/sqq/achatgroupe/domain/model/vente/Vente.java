package fr.sqq.achatgroupe.domain.model.vente;

import java.time.Instant;
import java.time.LocalDate;

public class Vente {

    private final Long id;
    private final String name;
    private final String description;
    private final VenteStatus status;
    private final Instant startDate;
    private final Instant endDate;
    private final PickupMode pickupMode;
    private final LocalDate pickupStartDate;
    private final LocalDate pickupEndDate;
    private final Instant createdAt;

    public Vente(Long id, String name, String description, VenteStatus status, Instant startDate, Instant endDate,
                 PickupMode pickupMode, LocalDate pickupStartDate, LocalDate pickupEndDate, Instant createdAt) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vente name must not be blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("Vente status must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Vente createdAt must not be null");
        }
        if (pickupMode == null) {
            throw new IllegalArgumentException("Vente pickupMode must not be null");
        }
        if (pickupMode == PickupMode.DATE_RANGE) {
            if (pickupStartDate == null || pickupEndDate == null) {
                throw new IllegalArgumentException("La plage de retrait est requise pour une vente sans créneau");
            }
            if (pickupEndDate.isBefore(pickupStartDate)) {
                throw new IllegalArgumentException("La date de fin de retrait doit être après la date de début");
            }
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickupMode = pickupMode;
        // En mode créneaux, la plage de retrait n'a pas de sens : on la normalise à null.
        this.pickupStartDate = pickupMode == PickupMode.DATE_RANGE ? pickupStartDate : null;
        this.pickupEndDate = pickupMode == PickupMode.DATE_RANGE ? pickupEndDate : null;
        this.createdAt = createdAt;
    }

    public static Vente create(String name, String description) {
        return new Vente(null, name, description, VenteStatus.ACTIVE, null, null,
                PickupMode.TIME_SLOTS, null, null, Instant.now());
    }

    public static Vente create(String name, String description, Instant startDate, Instant endDate) {
        return new Vente(null, name, description, VenteStatus.ACTIVE, startDate, endDate,
                PickupMode.TIME_SLOTS, null, null, Instant.now());
    }

    public static Vente create(String name, String description, Instant startDate, Instant endDate,
                               PickupMode pickupMode, LocalDate pickupStartDate, LocalDate pickupEndDate) {
        return new Vente(null, name, description, VenteStatus.ACTIVE, startDate, endDate,
                pickupMode, pickupStartDate, pickupEndDate, Instant.now());
    }

    public Vente activate() {
        if (this.status == VenteStatus.ACTIVE) {
            return this;
        }
        return new Vente(id, name, description, VenteStatus.ACTIVE, startDate, endDate,
                pickupMode, pickupStartDate, pickupEndDate, createdAt);
    }

    public Vente close() {
        if (this.status == VenteStatus.CLOSED) {
            return this;
        }
        return new Vente(id, name, description, VenteStatus.CLOSED, startDate, endDate,
                pickupMode, pickupStartDate, pickupEndDate, createdAt);
    }

    public Vente withDates(Instant startDate, Instant endDate) {
        return new Vente(id, name, description, status, startDate, endDate,
                pickupMode, pickupStartDate, pickupEndDate, createdAt);
    }

    public Vente withDetails(String name, String description, Instant startDate, Instant endDate,
                             PickupMode pickupMode, LocalDate pickupStartDate, LocalDate pickupEndDate) {
        return new Vente(id, name, description, status, startDate, endDate,
                pickupMode, pickupStartDate, pickupEndDate, createdAt);
    }

    public boolean usesTimeSlots() {
        return pickupMode == PickupMode.TIME_SLOTS;
    }

    public Long id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public VenteStatus status() { return status; }
    public Instant startDate() { return startDate; }
    public Instant endDate() { return endDate; }
    public PickupMode pickupMode() { return pickupMode; }
    public LocalDate pickupStartDate() { return pickupStartDate; }
    public LocalDate pickupEndDate() { return pickupEndDate; }
    public Instant createdAt() { return createdAt; }
}
