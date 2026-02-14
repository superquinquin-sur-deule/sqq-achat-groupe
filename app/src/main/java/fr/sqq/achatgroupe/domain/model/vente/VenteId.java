package fr.sqq.achatgroupe.domain.model.vente;

public record VenteId(Long value) {
    public VenteId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Vente ID must be a positive number");
        }
    }
}
