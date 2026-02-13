package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ListVentesUseCase;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListVentes implements ListVentesUseCase {

    private final VenteRepository venteRepository;

    public ListVentes(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public List<Vente> listActiveVentes() {
        return venteRepository.findAllActive();
    }
}
