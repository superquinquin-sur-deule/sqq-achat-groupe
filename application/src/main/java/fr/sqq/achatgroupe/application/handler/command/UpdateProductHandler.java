package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdateProductHandler implements CommandHandler<UpdateProductCommand, Product> {

    private final ProductRepository productRepository;

    public UpdateProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Product handle(UpdateProductCommand command) {
        Product existing = productRepository.findByIdAndVenteId(command.id(), command.venteId())
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
}
