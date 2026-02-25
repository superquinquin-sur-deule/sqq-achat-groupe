package fr.sqq.achatgroupe.application.port.out;

import java.util.Optional;

public interface ProductImageRepository {

    void save(Long productId, byte[] data, String contentType);

    Optional<ProductImageData> findByProductId(Long productId);

    void deleteByProductId(Long productId);

    record ProductImageData(byte[] data, String contentType) {}
}
