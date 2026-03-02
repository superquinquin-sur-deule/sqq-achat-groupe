package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class OrderIdempotencySteps {

    @Inject
    TestContext testContext;

    private String idempotencyKey;
    private String firstOrderId;
    private String secondOrderId;

    private String createOrderBody() {
        Long productId = testContext.productIds().get(0);
        Long timeSlotId = testContext.timeSlotIds().get(0);
        return """
                {
                    "customerFirstName": "Marie",
                    "customerLastName": "Dupont",
                    "email": "marie@exemple.fr",
                    "phone": "06 12 34 56 78",
                    "timeSlotId": %d,
                    "idempotencyKey": "%s",
                    "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(timeSlotId, idempotencyKey, productId);
    }

    @Quand("je crée une commande avec une clé d'idempotence")
    public void jeCreerUneCommandeAvecUneCleDidempotence() {
        idempotencyKey = UUID.randomUUID().toString();

        firstOrderId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createOrderBody())
                .when()
                .post("/api/ventes/" + testContext.venteId() + "/orders")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("data.id");
    }

    @Et("je recrée la même commande avec la même clé d'idempotence")
    public void jeRecreeLaMemeCommandeAvecLaMemeCleDidempotence() {
        secondOrderId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(createOrderBody())
                .when()
                .post("/api/ventes/" + testContext.venteId() + "/orders")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getString("data.id");
    }

    @Alors("les deux réponses contiennent le même identifiant de commande")
    public void lesDeuxReponsesContiennentLeMemeIdentifiantDeCommande() {
        assertEquals(firstOrderId, secondOrderId,
                "Les deux commandes doivent avoir le même identifiant (idempotence)");
    }
}
