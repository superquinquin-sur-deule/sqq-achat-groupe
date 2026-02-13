package fr.sqq.achatgroupe.infrastructure.in.rest.backoffice;

import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase.SupplierOrderLine;
import fr.sqq.achatgroupe.infrastructure.in.rest.dto.SupplierOrderLineResponse;

import java.util.List;

public class BackofficeSupplierOrderRestMapper {

    private BackofficeSupplierOrderRestMapper() {
    }

    public static List<SupplierOrderLineResponse> toResponse(List<SupplierOrderLine> lines) {
        return lines.stream()
                .map(BackofficeSupplierOrderRestMapper::toResponse)
                .toList();
    }

    public static SupplierOrderLineResponse toResponse(SupplierOrderLine line) {
        return new SupplierOrderLineResponse(line.productName(), line.supplier(), line.totalQuantity());
    }
}
