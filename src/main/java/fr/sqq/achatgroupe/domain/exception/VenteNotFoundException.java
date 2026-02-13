package fr.sqq.achatgroupe.domain.exception;

import fr.sqq.achatgroupe.domain.model.vente.VenteId;

public class VenteNotFoundException extends DomainException {

    private final VenteId venteId;

    public VenteNotFoundException(VenteId venteId) {
        super("Vente with id " + venteId.value() + " not found");
        this.venteId = venteId;
    }

    public VenteId venteId() {
        return venteId;
    }
}
