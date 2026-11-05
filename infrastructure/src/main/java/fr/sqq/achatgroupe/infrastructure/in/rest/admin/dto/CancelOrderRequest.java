package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequest(
        @NotBlank(message = "La raison de l'annulation est requise") String reason
) {}
