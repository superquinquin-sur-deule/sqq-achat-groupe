package fr.sqq.achatgroupe.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
