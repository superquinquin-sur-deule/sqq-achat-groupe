package fr.sqq.achatgroupe.infrastructure.out.pdf;

import fr.sqq.achatgroupe.application.query.GenerateDistributionListQuery.DistributionOrder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

class PdfBoxDistributionPdfGeneratorScreenshotTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2025-01-15T10:00:00Z"), ZoneId.of("Europe/Paris"));

    private final PdfBoxDistributionPdfGenerator generator = new PdfBoxDistributionPdfGenerator(FIXED_CLOCK);

    @Test
    void single_slot_with_three_orders() throws IOException {
        List<DistributionOrder> orders = List.of(
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "AG-2025-00000001", "Alice", "Dupont", "Samedi 18/01 — 10h00-11h00"),
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        "AG-2025-00000002", "Bob", "Martin", "Samedi 18/01 — 10h00-11h00"),
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000003"),
                        "AG-2025-00000003", "Claire", "Bernard", "Samedi 18/01 — 10h00-11h00")
        );

        byte[] pdf = generator.generate(orders);
        PdfScreenshotAssert.assertPdfMatchesScreenshot(pdf, "distribution-single-slot");
    }

    @Test
    void multiple_slots() throws IOException {
        List<DistributionOrder> orders = List.of(
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "AG-2025-00000001", "Alice", "Dupont", "Samedi 18/01 — 10h00-11h00"),
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        "AG-2025-00000002", "Bob", "Martin", "Samedi 18/01 — 10h00-11h00"),
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000003"),
                        "AG-2025-00000003", "Claire", "Bernard", "Samedi 18/01 — 11h00-12h00"),
                new DistributionOrder(UUID.fromString("00000000-0000-0000-0000-000000000004"),
                        "AG-2025-00000004", "David", "Leroy", "Samedi 18/01 — 11h00-12h00")
        );

        byte[] pdf = generator.generate(orders);
        PdfScreenshotAssert.assertPdfMatchesScreenshot(pdf, "distribution-multiple-slots");
    }
}
