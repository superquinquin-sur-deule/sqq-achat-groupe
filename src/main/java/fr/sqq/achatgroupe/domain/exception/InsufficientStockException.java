package fr.sqq.achatgroupe.domain.exception;

public class InsufficientStockException extends DomainException {

    private final String productName;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(String productName, int availableStock, int requestedQuantity) {
        super("Le produit '" + productName + "' n'a que " + availableStock + " unités disponibles (demandé : " + requestedQuantity + ")");
        this.productName = productName;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public String productName() { return productName; }
    public int availableStock() { return availableStock; }
    public int requestedQuantity() { return requestedQuantity; }
}
