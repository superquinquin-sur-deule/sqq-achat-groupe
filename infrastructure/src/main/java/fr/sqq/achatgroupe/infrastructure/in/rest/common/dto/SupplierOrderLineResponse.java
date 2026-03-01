package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

public record SupplierOrderLineResponse(String reference, String productName, String brand, String supplier, int totalQuantity, Integer colisage, Integer nombreColis) {
}
