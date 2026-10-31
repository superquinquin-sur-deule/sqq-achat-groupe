package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateProductCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.OrderRepository.VariantOrderedQuantity;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.service.ProductVariantFactory;
import fr.sqq.achatgroupe.domain.exception.ProductModificationForbiddenException;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.StockBelowOrderedQuantityException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Log.infof("Updating product %s for %s", command.id(), command.venteId());
        Product existing = productRepository.findByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));

        List<ProductVariant> variants = ProductVariantFactory.toDomain(existing.id(), command.variants());

        if (orderRepository.existsNonCancelledByVenteId(command.venteId())) {
            checkLockedFields(existing, command, variants);
            checkStockAboveOrderedQuantities(existing, command, variants);
        }

        Product updated = new Product(
                existing.id(),
                existing.venteId(),
                command.name(),
                command.description(),
                command.tauxTva(),
                command.supplier(),
                command.active(),
                command.category(),
                command.brand(),
                command.importantNote(),
                existing.hasImage(),
                variants
        );
        productRepository.save(updated);
        return updated;
    }

    /**
     * Dès qu'une commande non annulée existe sur la vente, les champs qui ont servi à établir
     * les commandes et le bon fournisseur sont gelés. L'ajout d'une variante reste autorisé
     * (purement additif), sa suppression ne l'est pas.
     */
    private void checkLockedFields(Product existing, UpdateProductCommand command, List<ProductVariant> variants) {
        boolean productFieldChanged = !Objects.equals(existing.name(), command.name())
                || existing.tauxTva().compareTo(command.tauxTva()) != 0
                || !Objects.equals(existing.supplier(), command.supplier())
                || !Objects.equals(existing.category(), command.category())
                || !Objects.equals(existing.brand(), command.brand());
        if (productFieldChanged) {
            throw new ProductModificationForbiddenException();
        }

        Map<Long, ProductVariant> submitted = variants.stream()
                .filter(v -> v.id() != null)
                .collect(Collectors.toMap(ProductVariant::id, Function.identity()));

        for (ProductVariant before : existing.variants()) {
            ProductVariant after = submitted.get(before.id());
            if (after == null) {
                // suppression d'une variante déjà commandable
                throw new ProductModificationForbiddenException();
            }
            boolean variantFieldChanged = !Objects.equals(before.label(), after.label())
                    || !Objects.equals(before.reference(), after.reference())
                    || before.prixHt().amount().compareTo(after.prixHt().amount()) != 0
                    || !Objects.equals(before.colisage(), after.colisage());
            if (variantFieldChanged) {
                throw new ProductModificationForbiddenException();
            }
        }
    }

    private void checkStockAboveOrderedQuantities(Product existing, UpdateProductCommand command,
                                                  List<ProductVariant> variants) {
        Map<Long, Long> orderedByVariant = new HashMap<>();
        for (VariantOrderedQuantity quantity : orderRepository.findEffectiveOrderedQuantities(command.venteId())) {
            orderedByVariant.put(quantity.variantId(), quantity.effectiveQuantity());
        }

        for (ProductVariant variant : variants) {
            if (variant.id() == null) {
                continue;
            }
            long ordered = orderedByVariant.getOrDefault(variant.id(), 0L);
            if (variant.stock() < ordered) {
                throw new StockBelowOrderedQuantityException(
                        existing.displayName(variant), variant.stock(), ordered);
            }
        }
    }
}
