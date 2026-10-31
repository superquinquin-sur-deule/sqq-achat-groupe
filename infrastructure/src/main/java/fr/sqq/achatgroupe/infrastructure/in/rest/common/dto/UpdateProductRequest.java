package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        @NotBlank(message = "Le nom est requis") String name,
        String description,
        @NotNull(message = "Le taux de TVA est requis") @DecimalMin(value = "0", message = "Le taux de TVA ne peut pas être négatif") BigDecimal tauxTva,
        @NotBlank(message = "Le fournisseur est requis") String supplier,
        boolean active,
        @NotBlank(message = "La catégorie est requise") String category,
        @NotBlank(message = "La marque est requise") String brand,
        @Size(max = 500, message = "La mention importante ne peut pas dépasser 500 caractères") String importantNote,
        @NotEmpty(message = "Au moins une variante est requise") @Valid List<ProductVariantRequest> variants
) {}
