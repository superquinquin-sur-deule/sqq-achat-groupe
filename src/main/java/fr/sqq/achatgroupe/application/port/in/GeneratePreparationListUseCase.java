package fr.sqq.achatgroupe.application.port.in;

import java.util.List;

public interface GeneratePreparationListUseCase {

    List<PreparationOrder> generatePreparationList(Long venteId);

    record PreparationOrder(Long orderId, String orderNumber, String customerName, String timeSlotLabel,
                            List<PreparationItem> items) {}

    record PreparationItem(String productName, int quantity) {}
}
