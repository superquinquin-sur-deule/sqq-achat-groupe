package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateProductCommand;
import fr.sqq.achatgroupe.application.port.out.PaymentCatalogGateway;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateProductHandler implements CommandHandler<CreateProductCommand, Product> {

    private final ProductRepository productRepository;
    private final PaymentCatalogGateway paymentCatalogGateway;

    public CreateProductHandler(ProductRepository productRepository, PaymentCatalogGateway paymentCatalogGateway) {
        this.productRepository = productRepository;
        this.paymentCatalogGateway = paymentCatalogGateway;
    }

    @Override
    @Transactional
    public Product handle(CreateProductCommand command) {
        Log.infof("Creating product %d for %s", command.name(), command.venteId());
        Product product = new Product(
                null,
                command.venteId(),
                command.name(),
                command.description(),
                command.prixHt(),
                command.tauxTva(),
                command.supplier(),
                command.stock(),
                true,
                command.reference(),
                command.category(),
                command.brand(),
                command.colisage(),
                command.stripeTaxCode(),
                false
        );
        Product saved = productRepository.saveNew(product);
        String stripeProductId = paymentCatalogGateway.registerProduct(
                saved.id(), saved.name(), saved.description(),
                saved.prixHt().amount(), saved.tauxTva(), saved.prixTtc().amount(), saved.reference(),
                saved.stripeTaxCode());
        saved.assignStripeProductId(stripeProductId);
        productRepository.save(saved);
        return saved;
    }
}
