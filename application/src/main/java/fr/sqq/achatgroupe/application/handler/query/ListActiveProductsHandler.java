package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.ListActiveProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ListActiveProductsHandler implements QueryHandler<ListActiveProductsQuery, CursorPage<Product>> {

    private final ProductRepository productRepository;

    public ListActiveProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CursorPage<Product> handle(ListActiveProductsQuery query) {
        return productRepository.findAllActiveByVenteId(query.venteId(), query.pageRequest());
    }
}
