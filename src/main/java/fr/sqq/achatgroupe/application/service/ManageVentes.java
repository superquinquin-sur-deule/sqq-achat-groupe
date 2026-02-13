package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.application.port.in.ManageVentesUseCase;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.achatgroupe.domain.model.vente.VenteId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ManageVentes implements ManageVentesUseCase {

    private final VenteRepository venteRepository;

    public ManageVentes(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    public List<Vente> listAllVentes() {
        return venteRepository.findAllVentes();
    }

    @Override
    public Vente getVente(VenteId id) {
        return venteRepository.findById(id)
                .orElseThrow(() -> new VenteNotFoundException(id));
    }

    @Override
    @Transactional
    public Vente createVente(CreateCommand cmd) {
        Vente vente = Vente.create(cmd.name(), cmd.description(), cmd.startDate(), cmd.endDate());
        return venteRepository.save(vente);
    }

    @Override
    @Transactional
    public Vente updateVente(UpdateCommand cmd) {
        Vente vente = venteRepository.findById(cmd.id())
                .orElseThrow(() -> new VenteNotFoundException(cmd.id()));
        Vente updated = vente.withDetails(cmd.name(), cmd.description(), cmd.startDate(), cmd.endDate());
        return venteRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteVente(VenteId id) {
        venteRepository.findById(id)
                .orElseThrow(() -> new VenteNotFoundException(id));
        venteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Vente activateVente(VenteId id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new VenteNotFoundException(id));
        Vente activated = vente.activate();
        return venteRepository.save(activated);
    }

    @Override
    @Transactional
    public Vente deactivateVente(VenteId id) {
        Vente vente = venteRepository.findById(id)
                .orElseThrow(() -> new VenteNotFoundException(id));
        Vente closed = vente.close();
        return venteRepository.save(closed);
    }
}
