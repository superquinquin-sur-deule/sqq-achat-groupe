package fr.sqq.achatgroupe.application.port.in;

public interface InitiatePaymentUseCase {

    PaymentSession execute(InitiatePaymentCommand command);

    record InitiatePaymentCommand(Long orderId, String successUrl, String cancelUrl) {}

    record PaymentSession(String checkoutUrl, String sessionId) {}
}
