package fr.sqq.achatgroupe.domain.exception;

public class TimeSlotHasReservationsException extends DomainException {

    public TimeSlotHasReservationsException(Long timeSlotId, int reserved) {
        super("Ce créneau a " + reserved + " réservation(s). Ajoutez ?force=true pour confirmer la suppression.");
    }
}
