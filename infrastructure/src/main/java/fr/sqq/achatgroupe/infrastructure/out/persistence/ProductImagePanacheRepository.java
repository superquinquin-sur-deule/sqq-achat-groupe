package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.application.port.out.ProductImageRepository;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ProductImageEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class ProductImagePanacheRepository implements ProductImageRepository, PanacheRepositoryBase<ProductImageEntity, Long> {

    @Override
    public void save(Long productId, byte[] data, String contentType) {
        ProductImageEntity entity = findById(productId);
        if (entity == null) {
            entity = new ProductImageEntity();
            entity.setProductId(productId);
            entity.setData(data);
            entity.setContentType(contentType);
            persist(entity);
        } else {
            entity.setData(data);
            entity.setContentType(contentType);
        }
    }

    @Override
    public Optional<ProductImageData> findByProductId(Long productId) {
        ProductImageEntity entity = findById(productId);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(new ProductImageData(entity.getData(), entity.getContentType()));
    }

    @Override
    public void deleteByProductId(Long productId) {
        deleteById(productId);
    }
}
