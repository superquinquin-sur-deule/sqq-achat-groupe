package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ManageCampaignUseCase;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ManageCampaign implements ManageCampaignUseCase {

    private final VenteRepository venteRepository;

    public ManageCampaign(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    @Transactional
    public Vente activateCampaign(VenteId venteId) {
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new VenteNotFoundException(venteId));
        Vente activated = vente.activate();
        return venteRepository.save(activated);
    }

    @Override
    @Transactional
    public Vente deactivateCampaign(VenteId venteId) {
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new VenteNotFoundException(venteId));
        Vente closed = vente.close();
        return venteRepository.save(closed);
    }

    @Override
    public Vente getCampaignStatus(VenteId venteId) {
        return venteRepository.findById(venteId)
                .orElseThrow(() -> new VenteNotFoundException(venteId));
    }

    @Override
    public List<Vente> listAllVentes() {
        return venteRepository.findAllVentes();
    }
}
