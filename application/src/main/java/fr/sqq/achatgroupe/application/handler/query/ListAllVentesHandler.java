package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.application.query.ListAllVentesQuery;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListAllVentesHandler implements QueryHandler<ListAllVentesQuery, List<Vente>> {

    private final VenteRepository venteRepository;

    public ListAllVentesHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public List<Vente> handle(ListAllVentesQuery query) {
        return venteRepository.findAllVentes();
    }
}
