package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.exception.ProductModificationForbiddenException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Objects;

@ApplicationScoped
public class UpdateProductHandler implements CommandHandler<UpdateProductCommand, Product> {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public UpdateProductHandler(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Product handle(UpdateProductCommand command) {
        Product existing = productRepository.findByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        if (orderRepository.existsNonCancelledByVenteId(command.venteId())) {
            boolean onlyActiveChanged = Objects.equals(existing.name(), command.name())
                    && Objects.equals(existing.description(), command.description())
                    && existing.prixHt().amount().compareTo(command.prixHt().amount()) == 0
                    && existing.tauxTva().compareTo(command.tauxTva()) == 0
                    && Objects.equals(existing.supplier(), command.supplier())
                    && existing.stock() == command.stock()
                    && Objects.equals(existing.reference(), command.reference())
                    && Objects.equals(existing.category(), command.category())
                    && Objects.equals(existing.brand(), command.brand());
            if (!onlyActiveChanged) {
                throw new ProductModificationForbiddenException();
            }
        }

        Product updated = new Product(
                existing.id(),
                existing.venteId(),
                command.name(),
                command.description(),
                command.prixHt(),
                command.tauxTva(),
                command.supplier(),
                command.stock(),
                command.active(),
                command.reference(),
                command.category(),
                command.brand(),
                existing.hasImage()
        );
        productRepository.save(updated);
        return updated;
    }
}
