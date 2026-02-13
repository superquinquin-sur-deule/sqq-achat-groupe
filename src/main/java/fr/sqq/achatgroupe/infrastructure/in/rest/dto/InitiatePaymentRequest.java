package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record InitiatePaymentRequest(
        @NotBlank String successUrl,
        @NotBlank String cancelUrl
) {
}
