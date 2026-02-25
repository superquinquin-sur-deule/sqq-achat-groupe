package fr.sqq.achatgroupe.acceptance.support;

import io.quarkiverse.cucumber.ScenarioScope;

import java.util.List;

@ScenarioScope
public class TestContext {

    private Long venteId;
    private Long emptyVenteId;
    private List<Long> productIds;
    private List<Long> timeSlotIds;

    public Long venteId() {
        return venteId;
    }

    public void setVenteId(Long venteId) {
        this.venteId = venteId;
    }

    public Long emptyVenteId() {
        return emptyVenteId;
    }

    public void setEmptyVenteId(Long emptyVenteId) {
        this.emptyVenteId = emptyVenteId;
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
        emptyVenteId = null;
        productIds = null;
        timeSlotIds = null;
    }
}
