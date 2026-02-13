package fr.sqq.achatgroupe.domain.exception;

public class TimeSlotNotFoundException extends DomainException {

    public TimeSlotNotFoundException(Long timeSlotId) {
        super("Créneau introuvable : " + timeSlotId);
    }
}
