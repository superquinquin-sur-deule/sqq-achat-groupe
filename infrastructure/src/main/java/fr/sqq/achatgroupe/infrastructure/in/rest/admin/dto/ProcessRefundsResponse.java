package fr.sqq.achatgroupe.infrastructure.in.rest.admin.dto;

import java.math.BigDecimal;

public record ProcessRefundsResponse(int totalProcessed, int totalSucceeded, int totalFailed,
                                     BigDecimal totalAmountRefunded) {}
