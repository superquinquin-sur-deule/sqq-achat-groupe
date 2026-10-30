package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateVenteCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteHasOrdersException;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdateVenteHandler implements CommandHandler<UpdateVenteCommand, Vente> {

    private final VenteRepository venteRepository;
    private final OrderRepository orderRepository;

    public UpdateVenteHandler(VenteRepository venteRepository, OrderRepository orderRepository) {
        this.venteRepository = venteRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Vente handle(UpdateVenteCommand cmd) {
        Log.info("Updating vente with id: " + cmd.id().value());
        Vente vente = venteRepository.findById(cmd.id())
                .orElseThrow(() -> new VenteNotFoundException(cmd.id()));

        // Le mode de retrait est verrouillé dès qu'une commande non annulée existe :
        // basculer rendrait incohérentes les commandes déjà rattachées à un créneau.
        if (cmd.pickupMode() != vente.pickupMode()
                && orderRepository.existsNonCancelledByVenteId(cmd.id().value())) {
            throw new VenteHasOrdersException(
                    "Impossible de changer le mode de retrait car la vente contient des commandes.");
        }

        Vente updated = vente.withDetails(cmd.name(), cmd.description(), cmd.startDate(), cmd.endDate(),
                cmd.pickupMode(), cmd.pickupStartDate(), cmd.pickupEndDate());
        return venteRepository.save(updated);
    }
}
