package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@QuarkusTest
public class ExpiredVenteRedirectSteps {

    @Inject
    TestContext testContext;

    @Étantdonnéque("une vente expirée existe")
    public void uneVenteExpireeExiste() {
        Instant startDate = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant endDate = Instant.now().minus(1, ChronoUnit.DAYS);

        Long venteId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Vente Expirée %d",
                            "description": "Vente dont la date de fin est passée",
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(System.nanoTime(), startDate, endDate))
                .when()
                .post("/api/admin/ventes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("data.id");

        testContext.setVenteId(venteId);
    }

    @Quand("j'accède à la page de la vente expirée")
    public void jAccedeALaPageDeLaVenteExpiree() {
        String venteUrl = PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId();
        PlaywrightHooks.page().navigate(venteUrl);
    }
}
