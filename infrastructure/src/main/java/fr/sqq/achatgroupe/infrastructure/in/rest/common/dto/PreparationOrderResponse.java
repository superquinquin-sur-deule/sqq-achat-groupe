package fr.sqq.achatgroupe.infrastructure.in.rest.common.dto;

import java.util.List;
import java.util.UUID;

public record PreparationOrderResponse(UUID orderId, String orderNumber, String customerFirstName,
                                       String customerLastName, String customerEmail,
                                       String customerPhone, String cooperatorNumber, String timeSlotLabel,
                                       List<PreparationItemResponse> items) {}
