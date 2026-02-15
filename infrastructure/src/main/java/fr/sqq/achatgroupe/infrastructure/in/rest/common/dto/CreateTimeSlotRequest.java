package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateTimeSlotRequest(
        @NotBlank(message = "La date est requise") String date,
        @NotBlank(message = "L'heure de début est requise") String startTime,
        @NotBlank(message = "L'heure de fin est requise") String endTime,
        @Min(value = 1, message = "La capacité doit être d'au moins 1") int capacity
) {}
