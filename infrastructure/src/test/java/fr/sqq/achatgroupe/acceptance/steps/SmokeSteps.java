package fr.sqq.achatgroupe.acceptance.steps;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class SmokeSteps {

    private Response response;

    @Étantdonnéque("l'application est démarrée")
    public void applicationDemarree() {
        // L'application est déjà démarrée par QuarkusTest
    }

    @Quand("j'accède au health check")
    public void accedeHealthCheck() {
        response = RestAssured.given()
                .when()
                .get("/q/health");
    }

    @Alors("le statut est {string}")
    public void leStatutEst(String expectedStatus) {
        response.then()
                .statusCode(200)
                .body("status", equalTo(expectedStatus));
    }
}
