package fr.sqq.achatgroupe.application.port.in;

import java.util.List;

public interface GenerateSupplierOrderUseCase {

    List<SupplierOrderLine> generateSupplierOrder(Long venteId);

    record SupplierOrderLine(String productName, String supplier, int totalQuantity) {}
}
