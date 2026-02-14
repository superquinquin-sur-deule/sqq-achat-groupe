package fr.sqq.achatgroupe.infrastructure.in.rest.admin.mapper;

import fr.sqq.achatgroupe.application.port.in.GenerateSupplierOrderUseCase.SupplierOrderLine;
import fr.sqq.achatgroupe.infrastructure.in.rest.common.dto.SupplierOrderLineResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface AdminSupplierOrderRestMapper {

    List<SupplierOrderLineResponse> toResponse(List<SupplierOrderLine> lines);

    SupplierOrderLineResponse toResponse(SupplierOrderLine line);
}
