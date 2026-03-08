package fr.sqq.achatgroupe.infrastructure.out.pdf;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationItem;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
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

    @Test
    void order_with_many_items_spans_multiple_pages() throws IOException {
        List<PreparationItem> items = new ArrayList<>();
        String[] suppliers = {"Ferme du Soleil", "Fromagerie des Monts", "Boulangerie Artisanale", "Maraîcher Bio"};
        String[][] products = {
                {"Pommes Golden 1kg", "Carottes bio 500g", "Tomates grappe 1kg", "Courgettes bio 500g",
                 "Poireaux botte", "Salade batavia", "Oignons rouges 500g", "Pommes de terre 2kg"},
                {"Fromage de chèvre", "Comté AOP 200g", "Beurre demi-sel 250g", "Yaourt nature x4",
                 "Crème fraîche 20cl", "Faisselle 500g", "Tomme de montagne 300g", "Raclette tranchée 400g"},
                {"Pain de campagne", "Baguette tradition", "Croissants x4", "Pain complet",
                 "Brioche tressée", "Fougasse aux olives", "Pain aux céréales", "Pain de mie"},
                {"Basilic botte", "Persil plat botte", "Ciboulette botte", "Menthe botte",
                 "Thym frais", "Romarin frais", "Épinards frais 500g", "Roquette 200g"}
        };

        for (int s = 0; s < suppliers.length; s++) {
            for (String product : products[s]) {
                items.add(new PreparationItem(product, suppliers[s], (s + 1), 0));
            }
        }
        // Add some cancelled items too
        items.add(new PreparationItem("Aubergines bio 500g", "Maraîcher Bio", 0, 2));
        items.add(new PreparationItem("Mozzarella di Bufala", "Fromagerie des Monts", 0, 1));

        PreparationOrder order = new PreparationOrder(
                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                "AG-2025-00000003",
                "Charlotte", "Lefebvre",
                "charlotte.lefebvre@email.com", "06 11 22 33 44",
                "Samedi 18/01 — 10h00-11h00",
                items
        );

        byte[] pdf = generator.generate(List.of(order));
        PdfScreenshotAssert.assertPdfMatchesScreenshot(pdf, "preparation-many-items");
    }
}
