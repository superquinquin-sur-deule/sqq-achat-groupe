package fr.sqq.achatgroupe.application.query;

public record CursorPageRequest(String cursor, int size) {

    public static CursorPageRequest first(int size) {
        return new CursorPageRequest(null, size);
    }

    public static CursorPageRequest after(String cursor, int size) {
        return new CursorPageRequest(cursor, size);
    }
}
