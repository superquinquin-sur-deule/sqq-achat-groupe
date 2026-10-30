package fr.sqq.achatgroupe.domain.exception;

public class TimeSlotRequiredException extends DomainException {

    public TimeSlotRequiredException() {
        super("Un créneau de retrait est requis pour cette vente");
    }
}
