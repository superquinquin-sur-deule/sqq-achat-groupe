package fr.sqq.achatgroupe.domain.model.reception;

public record ReceptionItem(Long id, Long productId, int orderedQuantity, int receivedQuantity) {

    public static ReceptionItem create(Long productId, int orderedQuantity, int receivedQuantity) {
        return new ReceptionItem(null, productId, orderedQuantity, receivedQuantity);
    }

    public int shortage() {
        return Math.max(0, orderedQuantity - receivedQuantity);
    }
}
