package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.application.port.out.TimeSlotRepository;
import fr.sqq.achatgroupe.domain.exception.ProductNotFoundException;
import fr.sqq.achatgroupe.domain.exception.TimeSlotNotFoundException;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductId;
import fr.sqq.achatgroupe.domain.model.order.CustomerInfo;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.achatgroupe.domain.model.order.OrderNumber;
import fr.sqq.achatgroupe.domain.model.planning.TimeSlot;
import fr.sqq.mediator.CommandHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CreateOrderHandler implements CommandHandler<CreateOrderCommand, Order> {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TimeSlotRepository timeSlotRepository;

    public CreateOrderHandler(OrderRepository orderRepository, ProductRepository productRepository, TimeSlotRepository timeSlotRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    @Transactional
    public Order handle(CreateOrderCommand command) {
        TimeSlot timeSlot = timeSlotRepository.findSlotById(command.timeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException(command.timeSlotId()));

        timeSlot.reserveOnePlace();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.items()) {
            Product product = productRepository.findById(new ProductId(itemCmd.productId()))
                    .orElseThrow(() -> new ProductNotFoundException(new ProductId(itemCmd.productId())));

            if (!product.active()) {
                throw new ProductNotFoundException(new ProductId(itemCmd.productId()));
            }

            product.decrementStock(itemCmd.quantity());
            productRepository.save(product);

            orderItems.add(OrderItem.create(itemCmd.productId(), itemCmd.quantity(), product.price()));
        }

        timeSlotRepository.save(timeSlot);

        CustomerInfo customer = new CustomerInfo(command.customerName(), command.email(), command.phone());
        OrderNumber orderNumber = OrderNumber.generate();
        Order order = Order.create(command.venteId(), orderNumber, customer, command.timeSlotId(), orderItems);

        return orderRepository.save(order);
    }
}
