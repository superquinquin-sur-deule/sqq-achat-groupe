package fr.sqq.achatgroupe.application.command;

import java.util.List;

public record ImportResult(
        int imported,
        List<ImportError> errors
) {

    public record ImportError(
            int line,
            String reason
    ) {
    }
}
