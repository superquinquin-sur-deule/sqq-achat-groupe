package fr.sqq.achatgroupe.infrastructure.out.excel;

import fr.sqq.achatgroupe.application.port.out.SupplierOrderExcelGenerator;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PoiSupplierOrderExcelGenerator implements SupplierOrderExcelGenerator {

    @Override
    public byte[] generate(List<SupplierOrderLine> lines) throws IOException {
        Map<String, List<SupplierOrderLine>> bySupplier = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (SupplierOrderLine line : lines) {
            bySupplier.computeIfAbsent(line.supplier(), k -> new java.util.ArrayList<>()).add(line);
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (Map.Entry<String, List<SupplierOrderLine>> entry : bySupplier.entrySet()) {
                String sheetName = sanitizeSheetName(toTitleCase(entry.getKey()));
                Sheet sheet = workbook.createSheet(sheetName);

                sheet.setColumnWidth(0, 15 * 256);
                sheet.setColumnWidth(1, 40 * 256);
                sheet.setColumnWidth(2, 20 * 256);
                sheet.setColumnWidth(3, 12 * 256);
                sheet.setColumnWidth(4, 12 * 256);
                sheet.setColumnWidth(5, 12 * 256);

                Row header = sheet.createRow(0);
                String[] headers = {"Référence", "Produit", "Marque", "Quantité", "Colisage", "Nb colis"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowIdx = 1;
                for (SupplierOrderLine line : entry.getValue()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(line.reference());
                    row.createCell(1).setCellValue(line.productName());
                    row.createCell(2).setCellValue(line.brand());
                    row.createCell(3).setCellValue(line.totalQuantity());
                    row.createCell(4).setCellValue(line.colisage() != null ? String.valueOf(line.colisage()) : "—");
                    row.createCell(5).setCellValue(line.nombreColis() != null ? String.valueOf(line.nombreColis()) : "—");
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String toTitleCase(String name) {
        String[] words = name.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return sb.toString();
    }

    private static String sanitizeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/:*?\\[\\]]", "_");
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized;
    }
}
