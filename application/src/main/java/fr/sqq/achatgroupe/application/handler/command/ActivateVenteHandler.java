package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ActivateVenteCommand;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ActivateVenteHandler implements CommandHandler<ActivateVenteCommand, Vente> {

    private final VenteRepository venteRepository;

    public ActivateVenteHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    @Transactional
    public Vente handle(ActivateVenteCommand command) {
        Vente vente = venteRepository.findById(command.id())
                .orElseThrow(() -> new VenteNotFoundException(command.id()));
        Vente activated = vente.activate();
        return venteRepository.save(activated);
    }
}
