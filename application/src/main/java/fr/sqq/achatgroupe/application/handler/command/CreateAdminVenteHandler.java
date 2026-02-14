package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateAdminVenteCommand;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateAdminVenteHandler implements CommandHandler<CreateAdminVenteCommand, Vente> {

    private final VenteRepository venteRepository;

    public CreateAdminVenteHandler(VenteRepository venteRepository) {
        this.venteRepository = venteRepository;
    }

    @Override
    @Transactional
    public Vente handle(CreateAdminVenteCommand cmd) {
        Vente vente = Vente.create(cmd.name(), cmd.description(), cmd.startDate(), cmd.endDate());
        return venteRepository.save(vente);
    }
}
