package fr.sqq.achatgroupe.application.query;

import fr.sqq.mediator.Query;

import java.util.List;

public record GenerateSupplierOrderQuery(Long venteId) implements Query<List<GenerateSupplierOrderQuery.SupplierOrderLine>> {

    public record SupplierOrderLine(String reference, String productName, String brand, String supplier, int totalQuantity, Integer colisage, Integer nombreColis) {
    }
}
