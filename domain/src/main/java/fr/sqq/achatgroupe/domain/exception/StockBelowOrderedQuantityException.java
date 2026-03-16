package fr.sqq.achatgroupe.domain.exception;

public class StockBelowOrderedQuantityException extends DomainException {

    public StockBelowOrderedQuantityException(String productName, int stock, long orderedQuantity) {
        super("Impossible de réduire le stock de \"" + productName + "\" à " + stock
                + " car " + orderedQuantity + " unité(s) sont déjà commandées.");
    }
}
