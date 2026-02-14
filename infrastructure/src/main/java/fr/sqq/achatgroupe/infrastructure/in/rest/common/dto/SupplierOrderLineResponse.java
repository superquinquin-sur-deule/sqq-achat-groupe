package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

public record SupplierOrderLineResponse(String productName, String supplier, int totalQuantity) {
}
