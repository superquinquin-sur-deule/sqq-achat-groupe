package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.util.List;

public record PreparationOrderResponse(Long orderId, String orderNumber, String customerName, String customerEmail,
                                       String customerPhone, String timeSlotLabel,
                                       List<PreparationItemResponse> items) {}
