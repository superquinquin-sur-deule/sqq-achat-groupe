package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull(message = "L'identifiant du produit est requis") Long productId,
        @Min(value = 1, message = "La quantité doit être au moins 1")
        @Max(value = 999, message = "La quantité ne peut pas dépasser 999")
        int quantity
) {
}
