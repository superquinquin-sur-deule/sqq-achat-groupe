package fr.sqq.achatgroupe.infrastructure.out.pdf;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationItem;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

class PdfBoxPreparationPdfGeneratorScreenshotTest {

    private final PdfBoxPreparationPdfGenerator generator = new PdfBoxPreparationPdfGenerator();

    @Test
    void order_with_items_from_multiple_suppliers() throws IOException {
        PreparationOrder order = new PreparationOrder(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "AG-2025-00000001",
                "Alice", "Dupont",
                "alice.dupont@email.com", "06 12 34 56 78",
                "Samedi 18/01 — 10h00-11h00",
                List.of(
                        new PreparationItem("Pommes Golden 1kg", "Ferme du Soleil", 2, 0),
                        new PreparationItem("Carottes bio 500g", "Ferme du Soleil", 1, 0),
                        new PreparationItem("Fromage de chèvre", "Fromagerie des Monts", 3, 0),
                        new PreparationItem("Yaourt nature x4", "Fromagerie des Monts", 1, 0)
                )
        );

        byte[] pdf = generator.generate(List.of(order));
        PdfScreenshotAssert.assertPdfMatchesScreenshot(pdf, "preparation-multiple-suppliers");
    }

    @Test
    void order_with_cancelled_items() throws IOException {
        PreparationOrder order = new PreparationOrder(
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "AG-2025-00000002",
                "Bob", "Martin",
                "bob.martin@email.com", "06 98 76 54 32",
                "Samedi 18/01 — 11h00-12h00",
                List.of(
                        new PreparationItem("Pommes Golden 1kg", "Ferme du Soleil", 2, 0),
                        new PreparationItem("Carottes bio 500g", "Ferme du Soleil", 0, 1),
                        new PreparationItem("Fromage de chèvre", "Fromagerie des Monts", 3, 0),
                        new PreparationItem("Yaourt nature x4", "Fromagerie des Monts", 0, 2)
                )
        );

        byte[] pdf = generator.generate(List.of(order));
        PdfScreenshotAssert.assertPdfMatchesScreenshot(pdf, "preparation-cancelled-items");
    }
}
