package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "ventes")
public class VenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "pickup_mode", nullable = false)
    private String pickupMode;

    @Column(name = "pickup_start_date")
    private LocalDate pickupStartDate;

    @Column(name = "pickup_end_date")
    private LocalDate pickupEndDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }

    public String getPickupMode() { return pickupMode; }
    public void setPickupMode(String pickupMode) { this.pickupMode = pickupMode; }

    public LocalDate getPickupStartDate() { return pickupStartDate; }
    public void setPickupStartDate(LocalDate pickupStartDate) { this.pickupStartDate = pickupStartDate; }

    public LocalDate getPickupEndDate() { return pickupEndDate; }
    public void setPickupEndDate(LocalDate pickupEndDate) { this.pickupEndDate = pickupEndDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
