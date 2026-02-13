package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

import java.util.List;

public record ImportProductsResponse(
        int imported,
        int errors,
        List<ImportErrorDetail> errorDetails
) {
    public record ImportErrorDetail(int line, String reason) {}
}
