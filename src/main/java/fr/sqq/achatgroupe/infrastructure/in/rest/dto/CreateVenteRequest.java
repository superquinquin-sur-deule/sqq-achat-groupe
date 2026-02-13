package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateVenteRequest(
        @NotBlank(message = "Le nom est requis") String name,
        String description,
        @NotEmpty(message = "Au moins un produit est requis") @Valid List<ProductRequest> products,
        @NotEmpty(message = "Au moins un créneau est requis") @Valid List<TimeSlotRequest> timeSlots
) {
    public record ProductRequest(
            @NotBlank(message = "Le nom du produit est requis") String name,
            String description,
            BigDecimal price,
            @NotBlank(message = "Le fournisseur est requis") String supplier,
            int stock
    ) {}

    public record TimeSlotRequest(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int capacity
    ) {}
}
