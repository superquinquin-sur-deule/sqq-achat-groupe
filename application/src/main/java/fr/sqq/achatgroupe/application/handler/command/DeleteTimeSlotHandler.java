package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.DeleteTimeSlotCommand;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.TimeSlotHasReservationsException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeleteTimeSlotHandler implements CommandHandler<DeleteTimeSlotCommand, Void> {

    private final TimeSlotRepository timeSlotRepository;

    public DeleteTimeSlotHandler(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public Void handle(DeleteTimeSlotCommand command) {
        TimeSlot existing = timeSlotRepository.findSlotByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new TimeSlotNotFoundException(command.id()));

        if (existing.reserved() > 0 && !command.force()) {
            throw new TimeSlotHasReservationsException(command.id(), existing.reserved());
        }

        timeSlotRepository.delete(command.id());
        return null;
    }
}
