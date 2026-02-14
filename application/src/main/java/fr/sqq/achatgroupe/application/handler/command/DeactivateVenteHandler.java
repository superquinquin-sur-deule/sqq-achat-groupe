package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeactivateVenteCommand;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeactivateVenteHandler implements CommandHandler<DeactivateVenteCommand, Vente> {

    private final VenteRepository venteRepository;

    public DeactivateVenteHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    @Transactional
    public Vente handle(DeactivateVenteCommand command) {
        Vente vente = venteRepository.findById(command.id())
                .orElseThrow(() -> new VenteNotFoundException(command.id()));
        Vente closed = vente.close();
        return venteRepository.save(closed);
    }
}
