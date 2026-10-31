package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "prix_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixHt;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "colisage")
    private Integer colisage;

    @Column(name = "position", nullable = false)
    private int position;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BigDecimal getPrixHt() { return prixHt; }
    public void setPrixHt(BigDecimal prixHt) { this.prixHt = prixHt; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Integer getColisage() { return colisage; }
    public void setColisage(Integer colisage) { this.colisage = colisage; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
