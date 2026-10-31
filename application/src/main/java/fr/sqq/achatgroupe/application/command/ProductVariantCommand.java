package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.shared.Money;

/** Une variante telle que soumise par l'admin. {@code id} est nul pour une nouvelle variante. */
public record ProductVariantCommand(
        Long id,
        String label,
        String reference,
        Money prixHt,
        int stock,
        Integer colisage
) {
}
