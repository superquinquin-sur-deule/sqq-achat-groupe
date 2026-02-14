package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateVenteCommand(
        String name,
        String description,
        List<ProductCommand> products,
        List<TimeSlotCommand> timeSlots
) implements Command<CreateVenteResult> {

    public record ProductCommand(String name, String description, BigDecimal price, String supplier, int stock) {
    }

    public record TimeSlotCommand(LocalDate date, LocalTime startTime, LocalTime endTime, int capacity) {
    }
}
