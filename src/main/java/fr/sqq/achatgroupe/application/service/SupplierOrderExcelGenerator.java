package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase.SupplierOrderLine;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SupplierOrderExcelGenerator {

    public byte[] generate(List<SupplierOrderLine> lines) throws IOException {
        Map<String, List<SupplierOrderLine>> bySupplier = new LinkedHashMap<>();
        for (SupplierOrderLine line : lines) {
            bySupplier.computeIfAbsent(line.supplier(), k -> new java.util.ArrayList<>()).add(line);
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (Map.Entry<String, List<SupplierOrderLine>> entry : bySupplier.entrySet()) {
                String sheetName = sanitizeSheetName(entry.getKey());
                Sheet sheet = workbook.createSheet(sheetName);

                sheet.setColumnWidth(0, 40 * 256);
                sheet.setColumnWidth(1, 12 * 256);

                Row header = sheet.createRow(0);
                Cell h0 = header.createCell(0);
                h0.setCellValue("Produit");
                h0.setCellStyle(headerStyle);
                Cell h1 = header.createCell(1);
                h1.setCellValue("Quantité");
                h1.setCellStyle(headerStyle);

                int rowIdx = 1;
                for (SupplierOrderLine line : entry.getValue()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(line.productName());
                    row.createCell(1).setCellValue(line.totalQuantity());
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String sanitizeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/:*?\\[\\]]", "_");
        if (sanitized.length() > 31) {
            sanitized = sanitized.substring(0, 31);
        }
        return sanitized;
    }
}
