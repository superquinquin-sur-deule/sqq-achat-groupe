package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

public record PreparationItemResponse(String productName, String supplier, int quantity, int cancelledQuantity) {}
