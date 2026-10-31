package fr.sqq.achatgroupe.acceptance.support;

import io.quarkiverse.cucumber.ScenarioScope;

import java.time.LocalDate;
import java.util.List;

@ScenarioScope
public class TestContext {

    private Long venteId;
    private Long emptyVenteId;
    private List<Long> productIds;
    private List<Long> variantIds;
    private List<Long> timeSlotIds;
    private LocalDate pickupStartDate;
    private LocalDate pickupEndDate;

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

    /** Variantes « par défaut » des produits de test, dans le même ordre que productIds. */
    public List<Long> variantIds() {
        return variantIds;
    }

    public void setVariantIds(List<Long> variantIds) {
        this.variantIds = variantIds;
    }

    public List<Long> timeSlotIds() {
        return timeSlotIds;
    }

    public void setTimeSlotIds(List<Long> timeSlotIds) {
        this.timeSlotIds = timeSlotIds;
    }

    public LocalDate pickupStartDate() {
        return pickupStartDate;
    }

    public LocalDate pickupEndDate() {
        return pickupEndDate;
    }

    public void setPickupRange(LocalDate pickupStartDate, LocalDate pickupEndDate) {
        this.pickupStartDate = pickupStartDate;
        this.pickupEndDate = pickupEndDate;
    }

    public void reset() {
        venteId = null;
        emptyVenteId = null;
        productIds = null;
        variantIds = null;
        timeSlotIds = null;
        pickupStartDate = null;
        pickupEndDate = null;
    }
}
