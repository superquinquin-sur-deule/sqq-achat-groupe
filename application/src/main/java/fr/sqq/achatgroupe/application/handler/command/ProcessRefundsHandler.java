package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ProcessRefundsCommand;
import fr.sqq.achatgroupe.application.command.ProcessRefundsCommand.RefundSummary;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway;
import fr.sqq.achatgroupe.application.port.out.PaymentGateway.RefundResult;
import fr.sqq.achatgroupe.application.port.out.PaymentRepository;
import fr.sqq.achatgroupe.application.port.out.RefundRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.payment.Payment;
import fr.sqq.achatgroupe.domain.model.payment.Refund;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProcessRefundsHandler implements CommandHandler<ProcessRefundsCommand, RefundSummary> {

    private static final Logger LOG = Logger.getLogger(ProcessRefundsHandler.class);

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentGateway paymentGateway;

    public ProcessRefundsHandler(OrderRepository orderRepository,
                                 PaymentRepository paymentRepository,
                                 RefundRepository refundRepository,
                                 PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional
    public RefundSummary handle(ProcessRefundsCommand command) {
        List<Order> paidOrders = orderRepository.findPaidByVenteId(command.venteId());
        List<Order> ordersToRefund = paidOrders.stream()
                .filter(o -> o.refundAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        int totalProcessed = 0;
        int totalSucceeded = 0;
        int totalFailed = 0;
        BigDecimal totalAmountRefunded = BigDecimal.ZERO;

        for (Order order : ordersToRefund) {
            if (refundRepository.findByOrderId(order.id()).isPresent()) {
                continue;
            }

            Payment payment = paymentRepository.findByOrderId(order.id()).orElse(null);
            if (payment == null || payment.stripePaymentId() == null) {
                LOG.warnf("Pas de paiement Stripe trouvé pour la commande %s", order.id());
                continue;
            }

            BigDecimal refundAmount = order.refundAmount();
            Refund refund = Refund.create(order.id(), refundAmount);

            try {
                long amountInCents = refundAmount.movePointRight(2).longValueExact();
                RefundResult result = paymentGateway.createRefund(payment.stripePaymentId(), amountInCents);

                if (result.succeeded()) {
                    refund.markAsSucceeded(result.stripeRefundId());
                    totalSucceeded++;
                    totalAmountRefunded = totalAmountRefunded.add(refundAmount);
                } else {
                    refund.markAsFailed();
                    totalFailed++;
                }
            } catch (Exception e) {
                LOG.errorf(e, "Erreur lors du remboursement de la commande %s", order.id());
                refund.markAsFailed();
                totalFailed++;
            }

            refundRepository.save(refund);
            totalProcessed++;
        }

        LOG.infof("Remboursements traités pour la vente %d : %d total, %d réussis, %d échoués",
                command.venteId(), totalProcessed, totalSucceeded, totalFailed);

        return new RefundSummary(totalProcessed, totalSucceeded, totalFailed, totalAmountRefunded);
    }
}
