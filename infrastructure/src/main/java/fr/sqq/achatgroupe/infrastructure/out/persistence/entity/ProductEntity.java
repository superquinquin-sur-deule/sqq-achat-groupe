package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "prix_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixHt;

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "vente_id", nullable = false)
    private Long venteId;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "has_image", nullable = false)
    private boolean hasImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrixHt() {
        return prixHt;
    }

    public void setPrixHt(BigDecimal prixHt) {
        this.prixHt = prixHt;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getVenteId() { return venteId; }
    public void setVenteId(Long venteId) { this.venteId = venteId; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public boolean isHasImage() { return hasImage; }
    public void setHasImage(boolean hasImage) { this.hasImage = hasImage; }
}
