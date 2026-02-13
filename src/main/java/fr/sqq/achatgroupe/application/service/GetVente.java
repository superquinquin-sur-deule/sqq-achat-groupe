package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.GetVenteUseCase;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetVente implements GetVenteUseCase {

    private final VenteRepository venteRepository;

    public GetVente(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public Vente getVente(VenteId id) {
        return venteRepository.findById(id)
                .orElseThrow(() -> new VenteNotFoundException(id));
    }
}
