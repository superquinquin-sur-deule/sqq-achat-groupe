package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ManageProductsUseCase;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ManageProducts implements ManageProductsUseCase {

    private final ProductRepository productRepository;

    public ManageProducts(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> listAllProducts(Long venteId) {
        return productRepository.findAllByVenteId(venteId);
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = new Product(
                null,
                command.venteId(),
                command.name(),
                command.description(),
                command.price(),
                command.supplier(),
                command.stock(),
                true
        );
        return productRepository.saveNew(product);
    }

    @Override
    @Transactional
    public Product updateProduct(UpdateProductCommand command) {
        Product existing = productRepository.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        Product updated = new Product(
                existing.id(),
                existing.venteId(),
                command.name(),
                command.description(),
                command.price(),
                command.supplier(),
                command.stock(),
                command.active()
        );
        productRepository.save(updated);
        return updated;
    }

    @Override
    @Transactional
    public ImportResult importProducts(ImportProductsCommand command) {
        List<ImportError> errors = new ArrayList<>();
        int imported = 0;

        for (int i = 0; i < command.rows().size(); i++) {
            CsvProductRow row = command.rows().get(i);
            int lineNumber = i + 2; // +2 because line 1 is header, data starts at line 2
            try {
                Product product = new Product(
                        null,
                        command.venteId(),
                        row.name(),
                        row.description(),
                        row.price(),
                        row.supplier(),
                        row.stock(),
                        true
                );
                productRepository.saveNew(product);
                imported++;
            } catch (IllegalArgumentException e) {
                errors.add(new ImportError(lineNumber, e.getMessage()));
            }
        }

        return new ImportResult(imported, errors);
    }

    @Override
    @Transactional
    public void deleteProduct(ProductId id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.deleteById(id);
    }
}
