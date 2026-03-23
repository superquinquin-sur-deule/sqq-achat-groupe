package fr.sqq.achatgroupe.infrastructure.out.excel;

import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoiSupplierOrderExcelGeneratorTest {

    private final PoiSupplierOrderExcelGenerator generator = new PoiSupplierOrderExcelGenerator();

    @Test
    void should_group_suppliers_case_insensitively_and_use_title_case() throws IOException {
        List<SupplierOrderLine> lines = List.of(
                new SupplierOrderLine("AZA-001", "Produit 1", "Marque A", "Azade", 3, null, null),
                new SupplierOrderLine("AZA-002", "Produit 2", "Marque A", "AZADE", 5, null, null)
        );

        byte[] bytes = generator.generate(lines);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Azade", workbook.getSheetName(0));
            assertEquals(2, workbook.getSheetAt(0).getLastRowNum()); // 1 header + 2 lignes
        }
    }

    @Test
    void should_normalize_supplier_name_to_title_case() throws IOException {
        List<SupplierOrderLine> lines = List.of(
                new SupplierOrderLine("TOM-001", "Tomates", "Ferme A", "FERME DU SOLEIL", 5, null, null)
        );

        byte[] bytes = generator.generate(lines);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals("Ferme Du Soleil", workbook.getSheetName(0));
        }
    }

    @Test
    void should_create_one_sheet_per_supplier() throws IOException {
        List<SupplierOrderLine> lines = List.of(
                new SupplierOrderLine("TOM-001", "Tomates", "Ferme A", "Ferme A", 5, null, null),
                new SupplierOrderLine("FRO-001", "Fromage", "Ferme B", "Ferme B", 2, null, null)
        );

        byte[] bytes = generator.generate(lines);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, workbook.getNumberOfSheets());
            assertEquals("Ferme A", workbook.getSheetName(0));
            assertEquals("Ferme B", workbook.getSheetName(1));
        }
    }
}
