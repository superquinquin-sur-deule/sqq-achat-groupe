package fr.sqq.achatgroupe.infrastructure.in.rest.dto;

public record ProblemDetailResponse(
        String type,
        String title,
        int status,
        String detail
) {
}
