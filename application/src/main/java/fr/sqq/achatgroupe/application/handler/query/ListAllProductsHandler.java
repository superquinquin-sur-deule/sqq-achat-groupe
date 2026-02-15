package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.ListAllProductsQuery;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ListAllProductsHandler implements QueryHandler<ListAllProductsQuery, CursorPage<Product>> {

    private final ProductRepository productRepository;

    public ListAllProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CursorPage<Product> handle(ListAllProductsQuery query) {
        return productRepository.findAllByVenteId(query.venteId(), query.pageRequest());
    }
}
