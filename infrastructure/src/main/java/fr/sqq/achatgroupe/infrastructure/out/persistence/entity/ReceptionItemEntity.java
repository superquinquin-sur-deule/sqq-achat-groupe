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

@Entity
@Table(name = "reception_items")
public class ReceptionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reception_id", nullable = false)
    private ReceptionEntity reception;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "ordered_quantity", nullable = false)
    private int orderedQuantity;

    @Column(name = "received_quantity", nullable = false)
    private int receivedQuantity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ReceptionEntity getReception() { return reception; }
    public void setReception(ReceptionEntity reception) { this.reception = reception; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(int orderedQuantity) { this.orderedQuantity = orderedQuantity; }

    public int getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(int receivedQuantity) { this.receivedQuantity = receivedQuantity; }
}
