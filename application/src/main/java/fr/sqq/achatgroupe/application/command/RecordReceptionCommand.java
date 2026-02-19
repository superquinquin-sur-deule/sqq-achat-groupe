package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.mediator.Command;

import java.util.List;

public record RecordReceptionCommand(
        Long venteId,
        String supplier,
        List<ReceptionLineCommand> lines
) implements Command<Reception> {

    public record ReceptionLineCommand(Long productId, int receivedQuantity) {}
}
