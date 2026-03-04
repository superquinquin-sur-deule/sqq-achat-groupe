package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeleteTimeSlotCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.TimeSlotHasReservationsException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteTimeSlotHandler implements CommandHandler<DeleteTimeSlotCommand, Void> {

    private final TimeSlotRepository timeSlotRepository;
    private final OrderRepository orderRepository;

    public DeleteTimeSlotHandler(TimeSlotRepository timeSlotRepository, OrderRepository orderRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Void handle(DeleteTimeSlotCommand command) {
        Log.infof("Deleting time slot %s for %s", command.id(), command.venteId());
        timeSlotRepository.findSlotByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new TimeSlotNotFoundException(command.id()));

        if (orderRepository.existsNonCancelledByTimeSlotId(command.id()) && !command.force()) {
            throw new TimeSlotHasReservationsException(command.id(), 0);
        }

        orderRepository.detachOrdersFromTimeSlot(command.id());
        timeSlotRepository.delete(command.id());
        return null;
    }
}
