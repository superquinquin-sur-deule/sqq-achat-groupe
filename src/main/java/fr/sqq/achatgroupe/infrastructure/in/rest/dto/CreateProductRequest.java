package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotNull(message = "L'identifiant de vente est requis") Long venteId,
        @NotBlank(message = "Le nom est requis") String name,
        String description,
        @NotNull(message = "Le prix est requis") @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0") BigDecimal price,
        @NotBlank(message = "Le fournisseur est requis") String supplier,
        @Min(value = 0, message = "Le stock ne peut pas être négatif") int stock
) {}
