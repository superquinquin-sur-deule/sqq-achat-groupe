package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ImportProductsCommand;
import fr.sqq.achatgroupe.application.command.ImportResult;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportProductsHandler implements CommandHandler<ImportProductsCommand, ImportResult> {

    private final ProductRepository productRepository;

    public ImportProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ImportResult handle(ImportProductsCommand command) {
        List<ImportResult.ImportError> errors = new ArrayList<>();
        int imported = 0;

        for (int i = 0; i < command.rows().size(); i++) {
            ImportProductsCommand.CsvProductRow row = command.rows().get(i);
            int lineNumber = i + 2;
            try {
                Product product = new Product(
                        null,
                        command.venteId(),
                        row.name(),
                        row.description(),
                        row.price(),
                        row.supplier(),
                        row.stock(),
                        true,
                        row.reference(),
                        row.category(),
                        row.brand(),
                        false
                );
                productRepository.saveNew(product);
                imported++;
            } catch (IllegalArgumentException e) {
                errors.add(new ImportResult.ImportError(lineNumber, e.getMessage()));
            }
        }

        return new ImportResult(imported, errors);
    }
}
