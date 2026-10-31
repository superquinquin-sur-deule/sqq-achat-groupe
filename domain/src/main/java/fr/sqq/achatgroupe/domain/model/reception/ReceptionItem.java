package fr.sqq.achatgroupe.domain.model.reception;

public record ReceptionItem(Long id, Long productId, Long variantId, int orderedQuantity, int receivedQuantity) {

    public static ReceptionItem create(Long productId, Long variantId, int orderedQuantity, int receivedQuantity) {
        return new ReceptionItem(null, productId, variantId, orderedQuantity, receivedQuantity);
    }

    public int shortage() {
        return Math.max(0, orderedQuantity - receivedQuantity);
    }
}
