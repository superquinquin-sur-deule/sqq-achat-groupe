package fr.sqq.achatgroupe.domain.model.vente;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VenteTest {
    
    @Test
    void shouldCreateVenteWithNameAndDescription() {
        // Arrange
        String name = "Test Vente";
        String description = "This is a test vente";

        // Act
        Vente vente = Vente.create(name, description);

        // Assert
        assertNull(vente.id());
        assertEquals(name, vente.name());
        assertEquals(description, vente.description());
        assertEquals(VenteStatus.ACTIVE, vente.status());
        assertNotNull(vente.createdAt());
        assertNull(vente.startDate());
        assertNull(vente.endDate());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create(null, "Description"));
        assertEquals("Vente name must not be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create(" ", "Description"));
        assertEquals("Vente name must not be blank", exception.getMessage());
    }
    
    @Test
    void shouldCreateVenteWithDates() {
        // Arrange
        String name = "Test Vente with Dates";
        String description = "This is a test vente with dates";
        Instant startDate = Instant.parse("2026-02-14T10:00:00Z");
        Instant endDate = Instant.parse("2026-02-15T10:00:00Z");

        // Act
        Vente vente = Vente.create(name, description, startDate, endDate);

        // Assert
        assertNull(vente.id());
        assertEquals(name, vente.name());
        assertEquals(description, vente.description());
        assertEquals(VenteStatus.ACTIVE, vente.status());
        assertNotNull(vente.createdAt());
        assertEquals(startDate, vente.startDate());
        assertEquals(endDate, vente.endDate());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNullWithDates() {
        // Arrange
        Instant startDate = Instant.parse("2026-02-14T10:00:00Z");
        Instant endDate = Instant.parse("2026-02-15T10:00:00Z");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create(null, "Description", startDate, endDate));
        assertEquals("Vente name must not be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlankWithDates() {
        // Arrange
        Instant startDate = Instant.parse("2026-02-14T10:00:00Z");
        Instant endDate = Instant.parse("2026-02-15T10:00:00Z");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create(" ", "Description", startDate, endDate));
        assertEquals("Vente name must not be blank", exception.getMessage());
    }
}