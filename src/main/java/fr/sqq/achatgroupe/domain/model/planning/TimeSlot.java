package fr.sqq.achatgroupe.domain.model.planning;

import fr.sqq.achatgroupe.domain.exception.TimeSlotFullException;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {

    private final Long id;
    private final Long venteId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int capacity;
    private int reserved;

    public TimeSlot(Long id, Long venteId, LocalDate date, LocalTime startTime, LocalTime endTime, int capacity, int reserved) {
        if (date == null) throw new IllegalArgumentException("TimeSlot date must not be null");
        if (startTime == null || endTime == null) throw new IllegalArgumentException("TimeSlot times must not be null");
        if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("End time must be after start time");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        if (reserved < 0) throw new IllegalArgumentException("Reserved must not be negative");
        this.id = id;
        this.venteId = venteId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.reserved = reserved;
    }

    public void reserveOnePlace() {
        if (reserved >= capacity) {
            throw new TimeSlotFullException(id, startTime, endTime);
        }
        this.reserved++;
    }

    public void releaseOnePlace() {
        if (reserved <= 0) {
            return; // Idempotent — nothing to release
        }
        this.reserved--;
    }

    public boolean isFull() {
        return reserved >= capacity;
    }

    public int remainingPlaces() {
        return capacity - reserved;
    }

    public Long id() { return id; }
    public Long venteId() { return venteId; }
    public LocalDate date() { return date; }
    public LocalTime startTime() { return startTime; }
    public LocalTime endTime() { return endTime; }
    public int capacity() { return capacity; }
    public int reserved() { return reserved; }
}
