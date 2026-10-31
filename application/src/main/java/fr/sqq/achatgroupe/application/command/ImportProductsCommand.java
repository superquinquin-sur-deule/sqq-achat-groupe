package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.mediator.Command;

import java.math.BigDecimal;
import java.util.List;

public record ImportProductsCommand(
        Long venteId,
        List<CsvProductRow> rows
) implements Command<ImportResult> {

    /**
     * Une ligne de CSV. Lorsque {@code variante} est renseigné, les lignes consécutives
     * partageant le même {@code name} forment un seul produit à plusieurs variantes.
     */
    public record CsvProductRow(
            String name,
            String description,
            Money prixHt,
            BigDecimal tauxTva,
            String supplier,
            int stock,
            String reference,
            String category,
            String brand,
            Integer colisage,
            String importantNote,
            String variante
    ) {
    }
}
