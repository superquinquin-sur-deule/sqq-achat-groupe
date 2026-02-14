package fr.sqq.achatgroupe.acceptance.steps;

import jakarta.enterprise.context.ApplicationScoped;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@ApplicationScoped
public class AdminAuthSteps {

    private Response response;

    @Quand("j'appelle l'endpoint admin me sans authentification")
    public void jAppelleEndpointAdminMeSansAuth() {
        response = RestAssured.given()
                .accept("application/json")
                .when()
                .get("/api/admin/me");
    }

    @Alors("l'accès est refusé")
    public void lAccesEstRefuse() {
        // 401 (OIDC actif, production) ou 403 (OIDC désactivé, test)
        response.then()
                .statusCode(anyOf(is(401), is(403)));
    }
}
