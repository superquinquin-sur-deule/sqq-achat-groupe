package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Le nom est requis") String name,
        String description,
        @NotNull(message = "Le prix HT est requis") @DecimalMin(value = "0.01", message = "Le prix HT doit être supérieur à 0") BigDecimal prixHt,
        @NotNull(message = "Le taux de TVA est requis") @DecimalMin(value = "0", message = "Le taux de TVA ne peut pas être négatif") BigDecimal tauxTva,
        @NotBlank(message = "Le fournisseur est requis") String supplier,
        @Min(value = 0, message = "Le stock ne peut pas être négatif") int stock,
        boolean active,
        @NotBlank(message = "La référence est requise") String reference,
        @NotBlank(message = "La catégorie est requise") String category,
        @NotBlank(message = "La marque est requise") String brand
) {}
