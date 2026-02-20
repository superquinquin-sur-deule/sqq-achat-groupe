package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.math.BigDecimal;
import java.util.List;

public record ImportProductsCommand(
        Long venteId,
        List<CsvProductRow> rows
) implements Command<ImportResult> {

    public record CsvProductRow(
            String name,
            String description,
            BigDecimal price,
            String supplier,
            int stock,
            String reference,
            String category,
            String brand
    ) {
    }
}
