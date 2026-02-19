package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receptions")
public class ReceptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "vente_id", nullable = false)
    private Long venteId;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "reception", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReceptionItemEntity> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVenteId() { return venteId; }
    public void setVenteId(Long venteId) { this.venteId = venteId; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<ReceptionItemEntity> getItems() { return items; }
    public void setItems(List<ReceptionItemEntity> items) { this.items = items; }
}
