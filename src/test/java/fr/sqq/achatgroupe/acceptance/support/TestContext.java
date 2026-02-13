package fr.sqq.achatgroupe.acceptance.support;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TestContext {

    private Long venteId;
    private List<Long> productIds;
    private List<Long> timeSlotIds;

    public Long venteId() {
        return venteId;
    }

    public void setVenteId(Long venteId) {
        this.venteId = venteId;
    }

    public List<Long> productIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public List<Long> timeSlotIds() {
        return timeSlotIds;
    }

    public void setTimeSlotIds(List<Long> timeSlotIds) {
        this.timeSlotIds = timeSlotIds;
    }

    public void reset() {
        venteId = null;
        productIds = null;
        timeSlotIds = null;
    }
}
