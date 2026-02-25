package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Mediator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class OrderSetupSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    @Inject
    OrderRepository orderRepository;

    @Étantdonnéque("les commandes suivantes existent")
    @Transactional
    public void lesCommandesSuivantesExistent(DataTable dataTable) {
        Long venteId = testContext.venteId();

        for (Map<String, String> row : dataTable.asMaps()) {
            String prenom = row.get("prénom");
            String nom = row.get("nom");
            String email = row.get("email");
            String telephone = row.get("téléphone");
            int creneauIndex = Integer.parseInt(row.get("créneau"));
            Long timeSlotId = testContext.timeSlotIds().get(creneauIndex);
            String statut = row.get("statut");

            List<OrderItemCommand> items = parseProducts(row.get("produits"));

            Order order = mediator.send(new CreateOrderCommand(
                    venteId, prenom, nom, email, telephone, timeSlotId, items
            ));

            if ("PAID".equals(statut) || "PICKED_UP".equals(statut)) {
                order.markAsPaid();
            }
            if ("PICKED_UP".equals(statut)) {
                order.markAsPickedUp();
            }

            orderRepository.save(order);
        }
    }

    private List<OrderItemCommand> parseProducts(String produits) {
        List<OrderItemCommand> items = new ArrayList<>();
        for (String entry : produits.split(",")) {
            String[] parts = entry.trim().split(":");
            int productIndex = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            Long productId = testContext.productIds().get(productIndex);
            items.add(new OrderItemCommand(productId, quantity));
        }
        return items;
    }
}
