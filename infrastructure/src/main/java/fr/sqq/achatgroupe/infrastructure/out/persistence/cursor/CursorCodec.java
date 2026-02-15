package fr.sqq.achatgroupe.infrastructure.out.persistence.cursor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.UUID;

public final class CursorCodec {

    private static final String SEPARATOR = "::";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private CursorCodec() {}

    public static String encode(String... fields) {
        String joined = String.join(SEPARATOR, fields);
        return ENCODER.encodeToString(joined.getBytes());
    }

    public static String[] decode(String cursor) {
        String decoded = new String(DECODER.decode(cursor));
        return decoded.split(SEPARATOR, -1);
    }

    // Vente: cursor = id (Long), sorted by id DESC
    public static String encodeVenteCursor(Long id) {
        return encode(id.toString());
    }

    public static Long decodeVenteCursorId(String cursor) {
        String[] parts = decode(cursor);
        return Long.parseLong(parts[0]);
    }

    // Product: cursor = id (Long), sorted by id ASC
    public static String encodeProductCursor(Long id) {
        return encode(id.toString());
    }

    public static Long decodeProductCursorId(String cursor) {
        String[] parts = decode(cursor);
        return Long.parseLong(parts[0]);
    }

    // TimeSlot: cursor = date + startTime + id, sorted by date ASC, startTime ASC, id ASC
    public static String encodeTimeSlotCursor(LocalDate date, LocalTime startTime, Long id) {
        return encode(date.toString(), startTime.toString(), id.toString());
    }

    public static LocalDate decodeTimeSlotCursorDate(String cursor) {
        String[] parts = decode(cursor);
        return LocalDate.parse(parts[0]);
    }

    public static LocalTime decodeTimeSlotCursorStartTime(String cursor) {
        String[] parts = decode(cursor);
        return LocalTime.parse(parts[1]);
    }

    public static Long decodeTimeSlotCursorId(String cursor) {
        String[] parts = decode(cursor);
        return Long.parseLong(parts[2]);
    }

    // Order: cursor = createdAt (Instant) + id (UUID), sorted by createdAt DESC, id DESC
    public static String encodeOrderCursor(Instant createdAt, UUID id) {
        return encode(createdAt.toString(), id.toString());
    }

    public static Instant decodeOrderCursorCreatedAt(String cursor) {
        String[] parts = decode(cursor);
        return Instant.parse(parts[0]);
    }

    public static UUID decodeOrderCursorId(String cursor) {
        String[] parts = decode(cursor);
        return UUID.fromString(parts[1]);
    }
}
