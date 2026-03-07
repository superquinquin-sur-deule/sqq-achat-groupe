package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;

import java.io.IOException;
import java.util.List;

public interface SupplierOrderExcelGenerator {
    byte[] generate(List<SupplierOrderLine> lines) throws IOException;
}
