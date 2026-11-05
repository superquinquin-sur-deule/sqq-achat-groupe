package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.application.command.MarkOrderPickedUpCommand;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.mediator.Mediator;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class CancelOrderSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    private UUID orderId;
    private int lastStatusCode;

    @Étantdonnéque("une commande confirmée existe sur le premier créneau avec {int} unités du premier produit")
    @Transactional
    public void uneCommandeConfirmeeExiste(int quantity) {
        Order order = mediator.send(new CreateOrderCommand(
                testContext.venteId(), "Camille", "Durand", "camille@exemple.fr", "0612345678",
                testContext.timeSlotIds().get(0), null,
                List.of(new OrderItemCommand(testContext.variantIds().get(0), quantity))
        ));
        orderId = order.id();
    }

    @Étantdonnéque("la commande est marquée comme récupérée")
    public void laCommandeEstMarqueeCommeRecuperee() {
        mediator.send(new MarkOrderPickedUpCommand(testContext.venteId(), orderId));
    }

    @Quand("j'annule la commande via l'API admin avec la raison {string}")
    public void jAnnuleLaCommandeAvecLaRaison(String reason) {
        lastStatusCode = cancel(testContext.venteId(), """
                {"reason": "%s"}
                """.formatted(reason));
    }

    @Quand("j'annule la commande via l'API admin sans raison")
    public void jAnnuleLaCommandeSansRaison() {
        lastStatusCode = cancel(testContext.venteId(), """
                {"reason": "  "}
                """);
    }

    @Quand("j'annule la commande via l'API admin sur une autre vente")
    public void jAnnuleLaCommandeSurUneAutreVente() {
        lastStatusCode = cancel(testContext.emptyVenteId(), """
                {"reason": "Mauvaise vente"}
                """);
    }

    @Alors("l'annulation renvoie le statut {int}")
    public void lAnnulationRenvoieLeStatut(int expectedStatus) {
        assertEquals(expectedStatus, lastStatusCode);
    }

    @Et("la commande est annulée avec la raison {string}")
    public void laCommandeEstAnnuleeAvecLaRaison(String expectedReason) {
        JsonPath detail = fetchOrderDetail();
        assertEquals("CANCELLED", detail.getString("data.status"));
        assertEquals(expectedReason, detail.getString("data.cancellationReason"));
    }

    @Et("la commande est toujours confirmée")
    public void laCommandeEstToujoursConfirmee() {
        assertEquals("CONFIRMED", fetchOrderDetail().getString("data.status"));
    }

    @Et("le stock du premier produit est de {int}")
    public void leStockDuPremierProduitEstDe(int expectedStock) {
        Long variantId = testContext.variantIds().get(0);
        List<Map<String, Object>> products = RestAssured.given()
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/products?size=100")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data");

        Integer stock = null;
        for (Map<String, Object> product : products) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> variants = (List<Map<String, Object>>) product.get("variants");
            for (Map<String, Object> variant : variants) {
                if (variantId.equals(Long.valueOf(variant.get("id").toString()))) {
                    stock = Integer.valueOf(variant.get("stock").toString());
                }
            }
        }
        assertEquals(Integer.valueOf(expectedStock), stock, "Stock de la variante " + variantId);
    }

    @Et("le premier créneau compte {int} place réservée")
    public void lePremierCreneauCompteDesPlacesReservees(int expectedReserved) {
        Long timeSlotId = testContext.timeSlotIds().get(0);
        List<Map<String, Object>> slots = RestAssured.given()
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/timeslots?size=100")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data");

        Integer reserved = slots.stream()
                .filter(slot -> timeSlotId.equals(Long.valueOf(slot.get("id").toString())))
                .map(slot -> Integer.valueOf(slot.get("reserved").toString()))
                .findFirst()
                .orElse(null);
        assertEquals(Integer.valueOf(expectedReserved), reserved, "Places réservées du créneau " + timeSlotId);
    }

    private int cancel(Long venteId, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/admin/ventes/" + venteId + "/orders/" + orderId + "/cancel")
                .then()
                .extract()
                .statusCode();
    }

    private JsonPath fetchOrderDetail() {
        return RestAssured.given()
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/orders/" + orderId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }
}
