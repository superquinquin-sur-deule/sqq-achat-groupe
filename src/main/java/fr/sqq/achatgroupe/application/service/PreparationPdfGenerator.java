package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationItem;
import fr.sqq.achatgroupe.application.port.in.GeneratePreparationListUseCase.PreparationOrder;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class PreparationPdfGenerator {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float HEADER_FONT_SIZE = 16;
    private static final float LABEL_FONT_SIZE = 10;
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

            // Title
            y = drawText(cs, "SuperQuinquin — Bon de préparation", fontBold, HEADER_FONT_SIZE, MARGIN, y);
            y -= LINE_HEIGHT;

            // Order info
            y = drawText(cs, "Commande : " + order.orderNumber(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            y = drawText(cs, "Créneau : " + order.timeSlotLabel(), fontRegular, TEXT_FONT_SIZE, MARGIN, y);
            y -= LINE_HEIGHT * 0.5f;

            // Customer info
            y = drawText(cs, "Coopérateur : " + order.customerName(), fontBold, TEXT_FONT_SIZE, MARGIN, y);
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

            // Table rows
            for (PreparationItem item : order.items()) {
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
