package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateVenteCommand;
import fr.sqq.achatgroupe.application.command.CreateVenteResult;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.application.port.out.VenteRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.achatgroupe.domain.model.vente.Vente;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CreateVenteHandler implements CommandHandler<CreateVenteCommand, CreateVenteResult> {

    private final VenteRepository venteRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public CreateVenteHandler(VenteRepository venteRepository, ProductRepository productRepository, TimeSlotRepository timeSlotRepository) {
        this.venteRepository = venteRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public CreateVenteResult handle(CreateVenteCommand command) {
        Vente vente = Vente.create(command.name(), command.description());
        vente = venteRepository.save(vente);

        List<Long> productIds = new ArrayList<>();
        for (CreateVenteCommand.ProductCommand pc : command.products()) {
            Product product = new Product(null, vente.id(), pc.name(), pc.description(), pc.price(), pc.supplier(), pc.stock(), true);
            Product saved = productRepository.saveNew(product);
            productIds.add(saved.id());
        }

        List<Long> timeSlotIds = new ArrayList<>();
        for (CreateVenteCommand.TimeSlotCommand tc : command.timeSlots()) {
            TimeSlot timeSlot = new TimeSlot(null, vente.id(), tc.date(), tc.startTime(), tc.endTime(), tc.capacity(), 0);
            TimeSlot saved = timeSlotRepository.saveNew(timeSlot);
            timeSlotIds.add(saved.id());
        }

        return new CreateVenteResult(vente, productIds, timeSlotIds);
    }
}
