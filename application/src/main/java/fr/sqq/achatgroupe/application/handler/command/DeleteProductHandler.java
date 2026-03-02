package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeleteProductCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductImageRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductModificationForbiddenException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DeleteProductHandler implements CommandHandler<DeleteProductCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(DeleteProductHandler.class);
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductImageRepository productImageRepository;

    public DeleteProductHandler(ProductRepository productRepository, OrderRepository orderRepository,
                                ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    @Transactional
    public Void handle(DeleteProductCommand command) {
        Log.infof("Deleting product %d for %s", command.id(), command.venteId());
        productRepository.findByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));
        if (orderRepository.existsNonCancelledByVenteId(command.venteId())) {
            throw new ProductModificationForbiddenException();
        }
        productImageRepository.deleteByProductId(command.id().value());
        productRepository.deleteById(command.id());
        return null;
    }
}
