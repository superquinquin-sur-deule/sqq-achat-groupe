package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.vente.Vente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CreateVenteUseCase {

    CreateVenteResult execute(CreateVenteCommand command);

    record CreateVenteCommand(
            String name,
            String description,
            List<ProductCommand> products,
            List<TimeSlotCommand> timeSlots
    ) {
        public record ProductCommand(String name, String description, BigDecimal price, String supplier, int stock) {}
        public record TimeSlotCommand(LocalDate date, LocalTime startTime, LocalTime endTime, int capacity) {}
    }

    record CreateVenteResult(
            Vente vente,
            List<Long> productIds,
            List<Long> timeSlotIds
    ) {}
}
