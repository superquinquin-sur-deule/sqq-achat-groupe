package fr.sqq.achatgroupe.application.command;

import fr.sqq.achatgroupe.domain.model.shared.Money;
import fr.sqq.mediator.Command;

public record ProcessRefundsCommand(Long venteId) implements Command<ProcessRefundsCommand.RefundSummary> {

    public record RefundSummary(int totalProcessed, int totalSucceeded, int totalFailed,
                                Money totalAmountRefunded) {}
}
