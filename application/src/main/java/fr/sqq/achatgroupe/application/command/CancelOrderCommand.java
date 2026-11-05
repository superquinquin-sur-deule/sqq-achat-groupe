package fr.sqq.achatgroupe.application.command;

import fr.sqq.mediator.Command;

import java.util.UUID;

/**
 * Annule une commande. Quand {@code venteId} est renseigné, la commande doit appartenir
 * à cette vente — c'est le cas des annulations déclenchées depuis le back-office.
 */
public record CancelOrderCommand(Long venteId, UUID orderId, String reason) implements Command<Void> {
    public CancelOrderCommand(UUID orderId) {
        this(null, orderId, null);
    }
}
