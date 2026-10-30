package fr.sqq.achatgroupe.domain.model.vente;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

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
        assertEquals(PickupMode.TIME_SLOTS, vente.pickupMode());
        assertTrue(vente.usesTimeSlots());
        assertNull(vente.pickupStartDate());
        assertNull(vente.pickupEndDate());
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

    @Test
    void shouldCreateVenteWithDateRangePickup() {
        // Arrange
        LocalDate pickupStart = LocalDate.of(2026, 12, 12);
        LocalDate pickupEnd = LocalDate.of(2026, 12, 15);

        // Act
        Vente vente = Vente.create("Vente sans créneau", "Retrait libre", null, null,
                PickupMode.DATE_RANGE, pickupStart, pickupEnd);

        // Assert
        assertEquals(PickupMode.DATE_RANGE, vente.pickupMode());
        assertFalse(vente.usesTimeSlots());
        assertEquals(pickupStart, vente.pickupStartDate());
        assertEquals(pickupEnd, vente.pickupEndDate());
    }

    @Test
    void shouldAcceptASingleDayPickupRange() {
        // Arrange
        LocalDate sameDay = LocalDate.of(2026, 12, 12);

        // Act
        Vente vente = Vente.create("Vente d'un jour", null, null, null,
                PickupMode.DATE_RANGE, sameDay, sameDay);

        // Assert
        assertEquals(sameDay, vente.pickupStartDate());
        assertEquals(sameDay, vente.pickupEndDate());
    }

    @Test
    void shouldThrowExceptionWhenDateRangePickupHasNoDates() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create("Vente sans créneau", null, null, null,
                        PickupMode.DATE_RANGE, null, null));
        assertEquals("La plage de retrait est requise pour une vente sans créneau", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDateRangePickupEndIsBeforeStart() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vente.create("Vente sans créneau", null, null, null,
                        PickupMode.DATE_RANGE, LocalDate.of(2026, 12, 15), LocalDate.of(2026, 12, 12)));
        assertEquals("La date de fin de retrait doit être après la date de début", exception.getMessage());
    }

    @Test
    void shouldIgnorePickupDatesWhenModeIsTimeSlots() {
        // Act : les dates de retrait n'ont pas de sens avec des créneaux, elles sont normalisées
        Vente vente = Vente.create("Vente créneaux", null, null, null,
                PickupMode.TIME_SLOTS, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 15));

        // Assert
        assertNull(vente.pickupStartDate());
        assertNull(vente.pickupEndDate());
    }

    @Test
    void shouldKeepPickupConfigurationWhenChangingStatus() {
        // Arrange
        Vente vente = Vente.create("Vente sans créneau", null, null, null,
                PickupMode.DATE_RANGE, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 15));

        // Act
        Vente closed = vente.close();

        // Assert
        assertEquals(VenteStatus.CLOSED, closed.status());
        assertEquals(PickupMode.DATE_RANGE, closed.pickupMode());
        assertEquals(LocalDate.of(2026, 12, 12), closed.pickupStartDate());
        assertEquals(LocalDate.of(2026, 12, 15), closed.pickupEndDate());
    }

    @Test
    void shouldSwitchBackToTimeSlotsWithDetails() {
        // Arrange
        Vente vente = Vente.create("Vente sans créneau", null, null, null,
                PickupMode.DATE_RANGE, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 15));

        // Act
        Vente updated = vente.withDetails("Vente créneaux", null, null, null,
                PickupMode.TIME_SLOTS, null, null);

        // Assert
        assertTrue(updated.usesTimeSlots());
        assertNull(updated.pickupStartDate());
        assertNull(updated.pickupEndDate());
    }
}
