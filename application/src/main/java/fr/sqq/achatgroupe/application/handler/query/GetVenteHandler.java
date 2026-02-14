package fr.sqq.achatgroupe.application.handler.query;

import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.application.query.GetVenteQuery;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetVenteHandler implements QueryHandler<GetVenteQuery, Vente> {

    private final VenteRepository venteRepository;

    public GetVenteHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public Vente handle(GetVenteQuery query) {
        return venteRepository.findById(query.id())
                .orElseThrow(() -> new VenteNotFoundException(query.id()));
    }
}
