package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.query.GenerateDistributionListQuery.DistributionOrder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class DistributionPdfGenerator {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float TITLE_FONT_SIZE = 14;
    private static final float SLOT_HEADER_FONT_SIZE = 11;
    private static final float TEXT_FONT_SIZE = 10;
    private static final float CHECKBOX_SIZE = 8;
    private static final float BOTTOM_MARGIN = 60;

    public byte[] generate(List<DistributionOrder> orders) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            Map<String, List<DistributionOrder>> ordersBySlot = orders.stream()
                    .collect(Collectors.groupingBy(DistributionOrder::timeSlotLabel, LinkedHashMap::new, Collectors.toList()));

            PDPage page = newPage(document);
            PDPageContentStream cs = new PDPageContentStream(document, page);
            float y = page.getMediaBox().getHeight() - MARGIN;

            // Title
            String title = "SuperQuinquin \u2014 Liste de distribution \u2014 "
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            y = drawText(cs, title, fontBold, TITLE_FONT_SIZE, MARGIN, y);
            y -= LINE_HEIGHT * 0.5f;

            float pageWidth = page.getMediaBox().getWidth();
            float contentWidth = pageWidth - 2 * MARGIN;

            for (Map.Entry<String, List<DistributionOrder>> entry : ordersBySlot.entrySet()) {
                // Check if we need space for slot header + at least one order
                if (y < BOTTOM_MARGIN + LINE_HEIGHT * 3) {
                    cs.close();
                    page = newPage(document);
                    cs = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }

                // Slot header with grey background
                y -= LINE_HEIGHT * 0.5f;
                cs.setNonStrokingColor(0.85f, 0.85f, 0.85f);
                cs.addRect(MARGIN, y - 4, contentWidth, LINE_HEIGHT + 2);
                cs.fill();
                cs.setNonStrokingColor(0, 0, 0);
                drawText(cs, entry.getKey(), fontBold, SLOT_HEADER_FONT_SIZE, MARGIN + 5, y);
                y -= LINE_HEIGHT + 4;

                for (DistributionOrder order : entry.getValue()) {
                    if (y < BOTTOM_MARGIN) {
                        cs.close();
                        page = newPage(document);
                        cs = new PDPageContentStream(document, page);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }

                    // Checkbox
                    float cbX = MARGIN + 4;
                    float cbY = y + 1;
                    cs.setLineWidth(0.5f);
                    cs.addRect(cbX, cbY, CHECKBOX_SIZE, CHECKBOX_SIZE);
                    cs.stroke();

                    // NOM Prénom — AG-XXXX-XXXXX
                    String line = order.customerLastName().toUpperCase() + " " + order.customerFirstName()
                            + " \u2014 " + order.orderNumber();
                    drawText(cs, line, fontRegular, TEXT_FONT_SIZE, MARGIN + 20, y + 2);

                    y -= LINE_HEIGHT;
                }
            }

            cs.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private PDPage newPage(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private float drawText(PDPageContentStream cs, String text, PDType1Font font,
                           float fontSize, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - LINE_HEIGHT;
    }
}
