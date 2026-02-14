package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.GetProductQuery;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetProductHandler implements QueryHandler<GetProductQuery, Product> {

    private final ProductRepository productRepository;

    public GetProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product handle(GetProductQuery query) {
        return productRepository.findById(query.id())
                .orElseThrow(() -> new ProductNotFoundException(query.id()));
    }
}
