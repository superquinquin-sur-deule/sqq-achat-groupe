package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationItem;
import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class PreparationPdfGenerator {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float SUBTITLE_FONT_SIZE = 10;
    private static final float CUSTOMER_NAME_FONT_SIZE = 22;
    private static final float TEXT_FONT_SIZE = 10;
    private static final float TABLE_HEADER_FONT_SIZE = 9;
    private static final float TABLE_FONT_SIZE = 9;
    private static final float CHECKBOX_SIZE = 8;

    public byte[] generate(List<PreparationOrder> orders) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            for (PreparationOrder order : orders) {
                addOrderPage(document, order, fontBold, fontRegular);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private void addOrderPage(PDDocument document, PreparationOrder order,
                              PDType1Font fontBold, PDType1Font fontRegular) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float contentWidth = pageWidth - 2 * MARGIN;

        try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
            float y = page.getMediaBox().getHeight() - MARGIN;

            // Customer name in large bold — top of page
            String displayName = order.customerLastName().toUpperCase() + " " + order.customerFirstName();
            y = drawText(cs, displayName, fontBold, CUSTOMER_NAME_FONT_SIZE, MARGIN, y);

            // Subtitle aligned right
            String subtitle = "SuperQuinquin — Bon de préparation";
            float subtitleWidth = fontRegular.getStringWidth(subtitle) / 1000 * SUBTITLE_FONT_SIZE;
            drawText(cs, subtitle, fontRegular, SUBTITLE_FONT_SIZE, pageWidth - MARGIN - subtitleWidth, page.getMediaBox().getHeight() - MARGIN);
            y -= LINE_HEIGHT * 0.5f;

            // Order info
            y = drawText(cs, "Commande : " + order.orderNumber(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            y = drawText(cs, "Créneau : " + order.timeSlotLabel(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            y -= LINE_HEIGHT * 0.5f;
            if (order.customerEmail() != null && !order.customerEmail().isBlank()) {
                y = drawText(cs, "Email : " + order.customerEmail(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            }
            if (order.customerPhone() != null && !order.customerPhone().isBlank()) {
                y = drawText(cs, "Tél : " + order.customerPhone(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            }
            y -= LINE_HEIGHT;

            // Table
            float tableX = MARGIN;
            float checkboxColWidth = 25;
            float qtyColWidth = 50;
            float productColWidth = contentWidth - checkboxColWidth - qtyColWidth;

            // Table header line
            cs.setLineWidth(0.5f);
            cs.moveTo(tableX, y);
            cs.lineTo(tableX + contentWidth, y);
            cs.stroke();
            y -= LINE_HEIGHT;

            // Table header text
            drawText(cs, "OK", fontBold, TABLE_HEADER_FONT_SIZE, tableX + 4, y + 3);
            drawText(cs, "Produit", fontBold, TABLE_HEADER_FONT_SIZE, tableX + checkboxColWidth + 5, y + 3);
            drawText(cs, "Qté", fontBold, TABLE_HEADER_FONT_SIZE,
                    tableX + checkboxColWidth + productColWidth + 10, y + 3);

            y -= 4;
            cs.moveTo(tableX, y);
            cs.lineTo(tableX + contentWidth, y);
            cs.stroke();

            // Table rows grouped by supplier
            Map<String, List<PreparationItem>> itemsBySupplier = order.items().stream()
                    .collect(Collectors.groupingBy(PreparationItem::supplier, LinkedHashMap::new, Collectors.toList()));

            for (Map.Entry<String, List<PreparationItem>> entry : itemsBySupplier.entrySet()) {
                // Supplier header row
                y -= LINE_HEIGHT;
                cs.setNonStrokingColor(0.92f, 0.92f, 0.92f);
                cs.addRect(tableX, y - 2, contentWidth, LINE_HEIGHT);
                cs.fill();
                cs.setNonStrokingColor(0, 0, 0);
                drawText(cs, entry.getKey(), fontBold, TABLE_FONT_SIZE, tableX + 5, y + 3);
                y -= 4;
                cs.setLineWidth(0.2f);
                cs.moveTo(tableX, y);
                cs.lineTo(tableX + contentWidth, y);
                cs.stroke();

                for (PreparationItem item : entry.getValue()) {
                    y -= LINE_HEIGHT;

                    // Checkbox
                    float cbX = tableX + 6;
                    float cbY = y + 2;
                    cs.setLineWidth(0.5f);
                    cs.addRect(cbX, cbY, CHECKBOX_SIZE, CHECKBOX_SIZE);
                    cs.stroke();

                    // Product name
                    drawText(cs, item.productName(), fontRegular, TABLE_FONT_SIZE,
                            tableX + checkboxColWidth + 5, y + 3);

                    // Quantity
                    drawText(cs, String.valueOf(item.quantity()), fontRegular, TABLE_FONT_SIZE,
                            tableX + checkboxColWidth + productColWidth + 10, y + 3);

                    // Row separator
                    y -= 4;
                    cs.setLineWidth(0.2f);
                    cs.moveTo(tableX, y);
                    cs.lineTo(tableX + contentWidth, y);
                    cs.stroke();
                }
            }
        }
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
