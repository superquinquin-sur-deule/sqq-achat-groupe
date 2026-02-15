package fr.sqq.achatgroupe.infrastructure.out.persistence;

import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.CursorPageRequest;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import fr.sqq.achatgroupe.infrastructure.out.persistence.cursor.CursorCodec;
import fr.sqq.achatgroupe.infrastructure.out.persistence.entity.VenteEntity;
import fr.sqq.achatgroupe.infrastructure.out.persistence.mapper.VentePersistenceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class VentePanacheRepository implements VenteRepository, PanacheRepositoryBase<VenteEntity, Long> {

    @Inject
    VentePersistenceMapper mapper;

    @Override
    public Vente save(Vente vente) {
        VenteEntity entity = mapper.toEntity(vente);
        entity = getEntityManager().merge(entity);
        getEntityManager().flush();
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Vente> findById(VenteId id) {
        return find("id", id.value()).firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    public List<Vente> findAllActive() {
        return list("status = 'ACTIVE' AND (startDate IS NULL OR startDate <= CURRENT_TIMESTAMP) AND (endDate IS NULL OR endDate >= CURRENT_TIMESTAMP)").stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CursorPage<Vente> findAllActive(CursorPageRequest pageRequest) {
        var conditions = new ArrayList<String>();
        var params = new HashMap<String, Object>();

        conditions.add("status = 'ACTIVE' AND (startDate IS NULL OR startDate <= CURRENT_TIMESTAMP) AND (endDate IS NULL OR endDate >= CURRENT_TIMESTAMP)");

        if (pageRequest.cursor() != null) {
            Long cursorId = CursorCodec.decodeVenteCursorId(pageRequest.cursor());
            conditions.add("id < :cursorId");
            params.put("cursorId", cursorId);
        }

        String query = String.join(" AND ", conditions) + " ORDER BY id DESC";
        int fetchSize = pageRequest.size() + 1;

        List<VenteEntity> entities = find(query, params).range(0, fetchSize - 1).list();
        return buildCursorPage(entities, pageRequest.size());
    }

    @Override
    public List<Vente> findAllVentes() {
        return listAll(io.quarkus.panache.common.Sort.descending("id")).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public CursorPage<Vente> findAllVentes(CursorPageRequest pageRequest) {
        var params = new HashMap<String, Object>();
        String query;

        if (pageRequest.cursor() != null) {
            Long cursorId = CursorCodec.decodeVenteCursorId(pageRequest.cursor());
            query = "id < :cursorId ORDER BY id DESC";
            params.put("cursorId", cursorId);
        } else {
            query = "ORDER BY id DESC";
        }

        int fetchSize = pageRequest.size() + 1;
        List<VenteEntity> entities = find(query, params).range(0, fetchSize - 1).list();
        return buildCursorPage(entities, pageRequest.size());
    }

    @Override
    public void deleteById(VenteId id) {
        delete("id", id.value());
    }

    private CursorPage<Vente> buildCursorPage(List<VenteEntity> entities, int size) {
        boolean hasNext = entities.size() > size;
        List<VenteEntity> page = hasNext ? entities.subList(0, size) : entities;
        List<Vente> items = page.stream().map(mapper::toDomain).toList();

        String endCursor = null;
        if (!page.isEmpty()) {
            VenteEntity last = page.get(page.size() - 1);
            endCursor = CursorCodec.encodeVenteCursor(last.getId());
        }

        return new CursorPage<>(items, endCursor, hasNext);
    }
}
