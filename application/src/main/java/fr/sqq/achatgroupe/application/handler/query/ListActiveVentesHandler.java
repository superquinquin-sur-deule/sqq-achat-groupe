package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.application.query.CursorPage;
import fr.sqq.achatgroupe.application.query.ListActiveVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ListActiveVentesHandler implements QueryHandler<ListActiveVentesQuery, CursorPage<Vente>> {

    private final VenteRepository venteRepository;

    public ListActiveVentesHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public CursorPage<Vente> handle(ListActiveVentesQuery query) {
        return venteRepository.findAllActive(query.pageRequest());
    }
}
