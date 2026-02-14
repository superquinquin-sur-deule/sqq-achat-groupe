package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.ListAllProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListAllProductsHandler implements QueryHandler<ListAllProductsQuery, List<Product>> {

    private final ProductRepository productRepository;

    public ListAllProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> handle(ListAllProductsQuery query) {
        return productRepository.findAllByVenteId(query.venteId());
    }
}
