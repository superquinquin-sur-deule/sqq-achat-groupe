package fr.sqq.achatgroupe.application.query;

import java.util.List;

public record CursorPage<T>(List<T> items, String endCursor, boolean hasNext) {

    public static <T> CursorPage<T> empty() {
        return new CursorPage<>(List.of(), null, false);
    }
}
