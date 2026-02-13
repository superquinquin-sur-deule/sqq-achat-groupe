package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

public record SupplierOrderLineResponse(String productName, String supplier, int totalQuantity) {
}
