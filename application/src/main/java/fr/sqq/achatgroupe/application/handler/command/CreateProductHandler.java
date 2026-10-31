package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateProductCommand;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.service.ProductVariantFactory;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateProductHandler implements CommandHandler<CreateProductCommand, Product> {

    private final ProductRepository productRepository;

    public CreateProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product handle(CreateProductCommand command) {
        Log.infof("Creating product %s for %s", command.name(), command.venteId());
        Product product = new Product(
                null,
                command.venteId(),
                command.name(),
                command.description(),
                command.tauxTva(),
                command.supplier(),
                true,
                command.category(),
                command.brand(),
                command.importantNote(),
                false,
                ProductVariantFactory.toDomain(null, command.variants())
        );
        return productRepository.saveNew(product);
    }
}
