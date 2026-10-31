package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "vente_id", nullable = false)
    private Long venteId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "has_image", nullable = false)
    private boolean hasImage;

    @Column(name = "important_note", length = 500)
    private String importantNote;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("position, id")
    private List<ProductVariantEntity> variants = new ArrayList<>();

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getVenteId() { return venteId; }
    public void setVenteId(Long venteId) { this.venteId = venteId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public boolean isHasImage() { return hasImage; }
    public void setHasImage(boolean hasImage) { this.hasImage = hasImage; }

    public String getImportantNote() { return importantNote; }
    public void setImportantNote(String importantNote) { this.importantNote = importantNote; }

    public List<ProductVariantEntity> getVariants() { return variants; }
    public void setVariants(List<ProductVariantEntity> variants) { this.variants = variants; }


}
