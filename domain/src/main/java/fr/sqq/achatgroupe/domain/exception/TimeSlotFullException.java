package fr.sqq.achatgroupe.domain.exception;

import java.time.LocalTime;

public class TimeSlotFullException extends DomainException {

    public TimeSlotFullException(Long slotId, LocalTime startTime, LocalTime endTime) {
        super("Le créneau " + startTime + " — " + endTime + " est complet");
    }
}
