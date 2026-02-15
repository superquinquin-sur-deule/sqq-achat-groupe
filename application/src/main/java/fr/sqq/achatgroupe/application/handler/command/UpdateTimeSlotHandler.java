package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.UpdateTimeSlotCommand;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdateTimeSlotHandler implements CommandHandler<UpdateTimeSlotCommand, TimeSlot> {

    private final TimeSlotRepository timeSlotRepository;

    public UpdateTimeSlotHandler(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public TimeSlot handle(UpdateTimeSlotCommand command) {
        TimeSlot existing = timeSlotRepository.findSlotByIdAndVenteId(command.id(), command.venteId())
                .orElseThrow(() -> new TimeSlotNotFoundException(command.id()));

        TimeSlot updated = new TimeSlot(
                existing.id(),
                existing.venteId(),
                command.date(),
                command.startTime(),
                command.endTime(),
                command.capacity(),
                existing.reserved()
        );
        timeSlotRepository.save(updated);
        return updated;
    }
}
