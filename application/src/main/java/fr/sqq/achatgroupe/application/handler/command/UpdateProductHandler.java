package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.OrderRepository.ProductOrderedQuantity;
import fr.sqq.achatgroupe.domain.exception.ProductModificationForbiddenException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.StockBelowOrderedQuantityException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
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
        Log.infof("Updating product %d for %s", command.id(), command.venteId());
        Product existing = productRepository.findByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        if (orderRepository.existsNonCancelledByVenteId(command.venteId())) {
            boolean lockedFieldChanged = !Objects.equals(existing.name(), command.name())
                    || existing.prixHt().amount().compareTo(command.prixHt().amount()) != 0
                    || existing.tauxTva().compareTo(command.tauxTva()) != 0
                    || !Objects.equals(existing.supplier(), command.supplier())
                    || !Objects.equals(existing.reference(), command.reference())
                    || !Objects.equals(existing.category(), command.category())
                    || !Objects.equals(existing.brand(), command.brand())
                    || !Objects.equals(existing.colisage(), command.colisage())
                    || !Objects.equals(existing.stripeTaxCode(), command.stripeTaxCode());
            if (lockedFieldChanged) {
                throw new ProductModificationForbiddenException();
            }

            if (command.stock() < existing.stock()) {
                List<ProductOrderedQuantity> quantities = orderRepository.findEffectiveOrderedQuantities(command.venteId());
                long effectiveQuantity = quantities.stream()
                        .filter(q -> q.productId().equals(existing.id()))
                        .map(ProductOrderedQuantity::effectiveQuantity)
                        .findFirst()
                        .orElse(0L);
                if (command.stock() < effectiveQuantity) {
                    throw new StockBelowOrderedQuantityException(existing.name(), command.stock(), effectiveQuantity);
                }
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
                command.colisage(),
                command.stripeTaxCode(),
                existing.hasImage()
        );
        updated.assignStripeProductId(existing.stripeProductId());
        productRepository.save(updated);
        return updated;
    }
}
