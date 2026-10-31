package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Une variante soumise par l'admin. {@code id} est nul pour une variante à créer. */
public record ProductVariantRequest(
        Long id,
        @Size(max = 100, message = "Le libellé de variante ne peut pas dépasser 100 caractères") String label,
        @NotBlank(message = "La référence est requise") String reference,
        @NotNull(message = "Le prix HT est requis") @DecimalMin(value = "0.01", message = "Le prix HT doit être supérieur à 0") BigDecimal prixHt,
        @Min(value = 0, message = "Le stock ne peut pas être négatif") int stock,
        @Min(value = 1, message = "Le colisage doit être supérieur à 0") Integer colisage
) {}
