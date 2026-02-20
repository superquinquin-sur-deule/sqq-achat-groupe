package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.infrastructure.out.persistence.cursor.CursorCodec;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.ProductPersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ProductPanacheRepository implements ProductRepository, PanacheRepositoryBase<ProductEntity, Long> {

    @Inject
    ProductPersistenceMapper mapper;

    @Override
    public List<Product> findAllActiveByVenteId(Long venteId) {
        return list("active = true and venteId = ?1", venteId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CursorPage<Product> findAllActiveByVenteId(Long venteId, CursorPageRequest pageRequest) {
        var params = new HashMap<String, Object>();
        params.put("venteId", venteId);
        String query;

        if (pageRequest.cursor() != null) {
            Long cursorId = CursorCodec.decodeProductCursorId(pageRequest.cursor());
            query = "active = true AND venteId = :venteId AND id > :cursorId ORDER BY id ASC";
            params.put("cursorId", cursorId);
        } else {
            query = "active = true AND venteId = :venteId ORDER BY id ASC";
        }

        int fetchSize = pageRequest.size() + 1;
        List<ProductEntity> entities = find(query, params).range(0, fetchSize - 1).list();
        return buildCursorPage(entities, pageRequest.size());
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return find("id", id.value()).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findByIdAndVenteId(ProductId id, Long venteId) {
        return find("id = ?1 AND venteId = ?2", id.value(), venteId).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findAllByVenteId(Long venteId) {
        return list("venteId = ?1", venteId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CursorPage<Product> findAllByVenteId(Long venteId, CursorPageRequest pageRequest) {
        var params = new HashMap<String, Object>();
        params.put("venteId", venteId);
        String query;

        if (pageRequest.cursor() != null) {
            Long cursorId = CursorCodec.decodeProductCursorId(pageRequest.cursor());
            query = "venteId = :venteId AND id > :cursorId ORDER BY id ASC";
            params.put("cursorId", cursorId);
        } else {
            query = "venteId = :venteId ORDER BY id ASC";
        }

        int fetchSize = pageRequest.size() + 1;
        List<ProductEntity> entities = find(query, params).range(0, fetchSize - 1).list();
        return buildCursorPage(entities, pageRequest.size());
    }

    @Override
    public void save(Product product) {
        ProductEntity entity = findById(product.id());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setPrice(product.price());
        entity.setSupplier(product.supplier());
        entity.setStock(product.stock());
        entity.setActive(product.active());
        entity.setReference(product.reference());
        entity.setCategory(product.category());
        entity.setBrand(product.brand());
    }

    @Override
    public Product saveNew(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        persist(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public void deleteById(ProductId id) {
        delete("id", id.value());
    }

    private CursorPage<Product> buildCursorPage(List<ProductEntity> entities, int size) {
        boolean hasNext = entities.size() > size;
        List<ProductEntity> page = hasNext ? entities.subList(0, size) : entities;
        List<Product> items = page.stream().map(mapper::toDomain).toList();

        String endCursor = null;
        if (!page.isEmpty()) {
            ProductEntity last = page.get(page.size() - 1);
            endCursor = CursorCodec.encodeProductCursor(last.getId());
        }

        return new CursorPage<>(items, endCursor, hasNext);
    }
}
