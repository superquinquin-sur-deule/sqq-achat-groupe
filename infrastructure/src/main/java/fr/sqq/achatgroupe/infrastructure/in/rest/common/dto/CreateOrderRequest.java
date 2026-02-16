package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Le prénom est requis") String customerFirstName,
        @NotBlank(message = "Le nom est requis") String customerLastName,
        @NotBlank(message = "L'email est requis") @Email(message = "Email invalide") String email,
        @NotBlank(message = "Le téléphone est requis") String phone,
        @NotNull(message = "Le créneau est requis") Long timeSlotId,
        @NotEmpty(message = "La commande doit contenir au moins un article")
        @Size(max = 50, message = "La commande ne peut pas contenir plus de 50 articles")
        List<@Valid OrderItemRequest> items
) {
}
