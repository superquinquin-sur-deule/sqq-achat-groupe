package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateVenteCommand;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdateVenteHandler implements CommandHandler<UpdateVenteCommand, Vente> {

    private final VenteRepository venteRepository;

    public UpdateVenteHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    @Transactional
    public Vente handle(UpdateVenteCommand cmd) {
        Vente vente = venteRepository.findById(cmd.id())
                .orElseThrow(() -> new VenteNotFoundException(cmd.id()));
        Vente updated = vente.withDetails(cmd.name(), cmd.description(), cmd.startDate(), cmd.endDate());
        return venteRepository.save(updated);
    }
}
