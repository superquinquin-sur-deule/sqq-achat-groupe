package fr.sqq.achatgroupe.infrastructure.in.rest.admin.util;

import fr.sqq.achatgroupe.application.command.ImportProductsCommand.CsvProductRow;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class CsvProductParser {

    private static final Set<String> REQUIRED_COLUMNS = Set.of("nom", "prix", "fournisseur", "stock");
    private static final Set<String> ALL_COLUMNS = Set.of("nom", "description", "prix", "fournisseur", "stock");

    public CsvParseResult parse(InputStream inputStream) {
        List<CsvProductRow> rows = new ArrayList<>();
        List<CsvParseError> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return CsvParseResult.failure("Le fichier CSV est vide");
            }

            char separator = detectSeparator(headerLine);
            Map<String, Integer> columnIndex = parseHeader(headerLine, separator);

            if (columnIndex == null) {
                return CsvParseResult.failure(
                        "Le fichier doit être au format CSV avec les colonnes : nom, description, prix, fournisseur, stock");
            }

            for (String missing : REQUIRED_COLUMNS) {
                if (!columnIndex.containsKey(missing)) {
                    return CsvParseResult.failure(
                            "Le fichier doit être au format CSV avec les colonnes : nom, description, prix, fournisseur, stock");
                }
            }

            String line;
            int lineNumber = 2;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    lineNumber++;
                    continue;
                }
                try {
                    CsvProductRow row = parseLine(line, separator, columnIndex);
                    rows.add(row);
                } catch (CsvLineParseException e) {
                    errors.add(new CsvParseError(lineNumber, e.getMessage()));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            return CsvParseResult.failure("Erreur de lecture du fichier CSV");
        }

        return new CsvParseResult(rows, errors, null);
    }

    private char detectSeparator(String headerLine) {
        long semicolons = headerLine.chars().filter(c -> c == ';').count();
        long commas = headerLine.chars().filter(c -> c == ',').count();
        return semicolons > commas ? ';' : ',';
    }

    private Map<String, Integer> parseHeader(String headerLine, char separator) {
        String[] headers = splitLine(headerLine, separator);
        Map<String, Integer> index = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            String normalized = headers[i].strip().toLowerCase();
            if (ALL_COLUMNS.contains(normalized)) {
                index.put(normalized, i);
            }
        }

        if (!index.keySet().containsAll(REQUIRED_COLUMNS)) {
            return null;
        }

        return index;
    }

    private CsvProductRow parseLine(String line, char separator, Map<String, Integer> columnIndex) {
        String[] values = splitLine(line, separator);

        String name = getColumn(values, columnIndex, "nom");
        String description = getColumn(values, columnIndex, "description");
        String priceStr = getColumn(values, columnIndex, "prix");
        String supplier = getColumn(values, columnIndex, "fournisseur");
        String stockStr = getColumn(values, columnIndex, "stock");

        if (name == null || name.isBlank()) {
            throw new CsvLineParseException("Le nom est requis");
        }
        if (supplier == null || supplier.isBlank()) {
            throw new CsvLineParseException("Le fournisseur est requis");
        }

        BigDecimal price;
        try {
            String normalizedPrice = priceStr != null ? priceStr.strip().replace(',', '.') : "";
            if (normalizedPrice.isEmpty()) {
                throw new CsvLineParseException("Le prix est requis");
            }
            price = new BigDecimal(normalizedPrice);
        } catch (NumberFormatException e) {
            throw new CsvLineParseException("Le prix n'est pas un nombre valide");
        }

        int stock;
        try {
            String normalizedStock = stockStr != null ? stockStr.strip() : "";
            if (normalizedStock.isEmpty()) {
                throw new CsvLineParseException("Le stock est requis");
            }
            stock = Integer.parseInt(normalizedStock);
        } catch (NumberFormatException e) {
            throw new CsvLineParseException("Le stock doit être un nombre entier");
        }

        return new CsvProductRow(name.strip(), description != null ? description.strip() : "", price, supplier.strip(), stock);
    }

    private String getColumn(String[] values, Map<String, Integer> columnIndex, String columnName) {
        Integer idx = columnIndex.get(columnName);
        if (idx == null || idx >= values.length) {
            return null;
        }
        return values[idx];
    }

    private String[] splitLine(String line, char separator) {
        return line.split(String.valueOf(separator), -1);
    }

    public record CsvParseResult(
            List<CsvProductRow> rows,
            List<CsvParseError> errors,
            String globalError
    ) {
        static CsvParseResult failure(String message) {
            return new CsvParseResult(List.of(), List.of(), message);
        }

        public boolean hasGlobalError() {
            return globalError != null;
        }
    }

    public record CsvParseError(int line, String reason) {}

    private static class CsvLineParseException extends RuntimeException {
        CsvLineParseException(String message) {
            super(message);
        }
    }
}
