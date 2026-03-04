package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.CancelOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Mediator;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class DeleteTimeSlotSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    private UUID createdOrderId;
    private int deleteStatusCode;

    @Étantdonnéque("une commande existe sur le premier créneau")
    @Transactional
    public void uneCommandeExisteSurLePremierCreneau() {
        Long venteId = testContext.venteId();
        Long timeSlotId = testContext.timeSlotIds().get(0);
        Long productId = testContext.productIds().get(0);

        Order order = mediator.send(new CreateOrderCommand(
                venteId, "Test", "Utilisateur", "test@exemple.fr", "0612345678",
                timeSlotId, null, List.of(new OrderItemCommand(productId, 1))
        ));

        createdOrderId = order.id();
    }

    @Et("la commande est annulée")
    @Transactional
    public void laCommandeEstAnnulee() {
        mediator.send(new CancelOrderCommand(createdOrderId));
    }

    @Quand("je supprime le premier créneau via l'API admin")
    public void jeSupprimeLePremierCreneauViaLApiAdmin() {
        Long venteId = testContext.venteId();
        Long timeSlotId = testContext.timeSlotIds().get(0);

        deleteStatusCode = RestAssured.given()
                .when()
                .delete("/api/admin/ventes/" + venteId + "/timeslots/" + timeSlotId)
                .then()
                .extract()
                .statusCode();
    }

    @Alors("la suppression du créneau réussit avec le statut {int}")
    public void laSuppressionDuCreneauReussitAvecLeStatut(int expectedStatus) {
        assertEquals(expectedStatus, deleteStatusCode);
    }

    @Alors("la suppression du créneau échoue avec le statut {int}")
    public void laSuppressionDuCreneauEchoueAvecLeStatut(int expectedStatus) {
        assertEquals(expectedStatus, deleteStatusCode);
    }
}
