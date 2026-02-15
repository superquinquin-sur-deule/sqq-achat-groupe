package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeleteProductCommand;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteProductHandler implements CommandHandler<DeleteProductCommand, Void> {

    private final ProductRepository productRepository;

    public DeleteProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Void handle(DeleteProductCommand command) {
        productRepository.findByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));
        productRepository.deleteById(command.id());
        return null;
    }
}
