package fr.sqq.achatgroupe.infrastructure.out.persistence.mapper;

import fr.sqq.achatgroupe.domain.model.reception.Reception;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionItem;
import fr.sqq.achatgroupe.domain.model.reception.ReceptionStatus;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ReceptionEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.ReceptionItemEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ReceptionPersistenceMapper {

    default Reception toDomain(ReceptionEntity entity) {
        List<ReceptionItem> items = entity.getItems().stream()
                .map(this::toDomain)
                .toList();

        return new Reception(
                entity.getId(),
                entity.getVenteId(),
                entity.getSupplier(),
                items,
                ReceptionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    default ReceptionItem toDomain(ReceptionItemEntity entity) {
        return new ReceptionItem(entity.getId(), entity.getProductId(),
                entity.getOrderedQuantity(), entity.getReceivedQuantity());
    }

    default ReceptionEntity toEntity(Reception domain) {
        var entity = new ReceptionEntity();
        entity.setId(domain.id());
        entity.setVenteId(domain.venteId());
        entity.setSupplier(domain.supplier());
        entity.setStatus(domain.status().name());
        entity.setCreatedAt(domain.createdAt());

        List<ReceptionItemEntity> itemEntities = domain.items().stream()
                .map(item -> toEntity(item, entity))
                .toList();
        entity.setItems(itemEntities);

        return entity;
    }

    default ReceptionItemEntity toEntity(ReceptionItem domain, ReceptionEntity receptionEntity) {
        var entity = new ReceptionItemEntity();
        entity.setId(domain.id());
        entity.setReception(receptionEntity);
        entity.setProductId(domain.productId());
        entity.setOrderedQuantity(domain.orderedQuantity());
        entity.setReceivedQuantity(domain.receivedQuantity());
        return entity;
    }
}
