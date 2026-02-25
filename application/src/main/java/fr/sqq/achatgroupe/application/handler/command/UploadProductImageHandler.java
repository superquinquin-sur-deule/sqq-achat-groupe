package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UploadProductImageCommand;
import fr.sqq.achatgroupe.application.port.out.ProductImageRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UploadProductImageHandler implements CommandHandler<UploadProductImageCommand, Product> {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public UploadProductImageHandler(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    @Transactional
    public Product handle(UploadProductImageCommand command) {
        Product existing = productRepository.findByIdAndVenteId(command.productId(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        productImageRepository.save(existing.id(), command.imageData(), command.contentType());

        Product updated = new Product(
                existing.id(),
                existing.venteId(),
                existing.name(),
                existing.description(),
                existing.price(),
                existing.supplier(),
                existing.stock(),
                existing.active(),
                existing.reference(),
                existing.category(),
                existing.brand(),
                true
        );
        productRepository.save(updated);
        return updated;
    }
}
