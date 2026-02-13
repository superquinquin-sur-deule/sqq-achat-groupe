package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;

import java.math.BigDecimal;
import java.util.List;

public interface ManageProductsUseCase {

    List<Product> listAllProducts(Long venteId);

    Product createProduct(CreateProductCommand command);

    Product updateProduct(UpdateProductCommand command);

    void deleteProduct(ProductId id);

    record CreateProductCommand(
            Long venteId,
            String name,
            String description,
            BigDecimal price,
            String supplier,
            int stock
    ) {}

    record UpdateProductCommand(
            ProductId id,
            String name,
            String description,
            BigDecimal price,
            String supplier,
            int stock,
            boolean active
    ) {}

    ImportResult importProducts(ImportProductsCommand command);

    record ImportProductsCommand(
            Long venteId,
            List<CsvProductRow> rows
    ) {}

    record CsvProductRow(
            String name,
            String description,
            BigDecimal price,
            String supplier,
            int stock
    ) {}

    record ImportResult(
            int imported,
            List<ImportError> errors
    ) {}

    record ImportError(
            int line,
            String reason
    ) {}
}
