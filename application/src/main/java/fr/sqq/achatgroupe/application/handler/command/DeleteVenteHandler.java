package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeleteVenteCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.exception.VenteHasOrdersException;
import fr.sqq.achatgroupe.domain.exception.VenteNotFoundException;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteVenteHandler implements CommandHandler<DeleteVenteCommand, Void> {

    private final VenteRepository venteRepository;
    private final OrderRepository orderRepository;

    public DeleteVenteHandler(VenteRepository venteRepository, OrderRepository orderRepository) {
        this.venteRepository = venteRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Void handle(DeleteVenteCommand command) {
        venteRepository.findById(command.id())
                .orElseThrow(() -> new VenteNotFoundException(command.id()));
        if (orderRepository.existsNonCancelledByVenteId(command.id().value())) {
            throw new VenteHasOrdersException();
        }
        venteRepository.deleteById(command.id());
        return null;
    }
}
