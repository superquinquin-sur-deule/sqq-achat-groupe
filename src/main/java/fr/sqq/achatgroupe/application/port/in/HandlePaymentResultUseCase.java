package fr.sqq.achatgroupe.application.port.in;

public interface HandlePaymentResultUseCase {

    void execute(HandlePaymentResultCommand command);

    record HandlePaymentResultCommand(String payload, String signature) {}
}
