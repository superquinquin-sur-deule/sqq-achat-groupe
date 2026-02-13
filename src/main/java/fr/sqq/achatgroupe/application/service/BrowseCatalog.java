package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.application.port.in.BrowseCatalogUseCase;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BrowseCatalog implements BrowseCatalogUseCase {

    private final ProductRepository productRepository;

    public BrowseCatalog(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> listActiveProducts(Long venteId) {
        return productRepository.findAllActiveByVenteId(venteId);
    }

    @Override
    public Product getProduct(ProductId id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
