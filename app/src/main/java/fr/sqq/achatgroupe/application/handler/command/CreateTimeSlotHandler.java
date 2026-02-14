package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateTimeSlotCommand;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateTimeSlotHandler implements CommandHandler<CreateTimeSlotCommand, TimeSlot> {

    private final TimeSlotRepository timeSlotRepository;

    public CreateTimeSlotHandler(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public TimeSlot handle(CreateTimeSlotCommand command) {
        TimeSlot timeSlot = new TimeSlot(
                null,
                command.venteId(),
                command.date(),
                command.startTime(),
                command.endTime(),
                command.capacity(),
                0
        );
        return timeSlotRepository.saveNew(timeSlot);
    }
}
