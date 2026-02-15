package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class VenteSteps {

    @Inject
    TestContext testContext;

    @Étantdonnéque("une vente existe avec des produits et des créneaux")
    public void uneVenteExisteAvecDesProduitsEtDesCreneaux() {
        createVente();
    }

    private void createVente() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);

        // 1. Create vente via admin endpoint
        String venteBody = """
                {
                    "name": "Vente Test %d",
                    "description": "Vente de test automatisée",
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(System.nanoTime(), startDate, endDate);

        Long venteId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(venteBody)
                .when()
                .post("/api/admin/ventes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("data.id");

        testContext.setVenteId(venteId);

        // 2. Create products via admin endpoint
        List<String> products = List.of(
                """
                {"venteId": %d, "name": "Tomates bio", "description": "Tomates grappe biologiques cultivées localement", "price": 3.50, "supplier": "Ferme du Soleil", "stock": 25}
                """.formatted(venteId),
                """
                {"venteId": %d, "name": "Pain de campagne", "description": "Pain artisanal au levain naturel, 500g", "price": 4.20, "supplier": "Boulangerie Martin", "stock": 15}
                """.formatted(venteId),
                """
                {"venteId": %d, "name": "Miel de fleurs", "description": "Miel toutes fleurs récolté en Île-de-France, 250g", "price": 8.90, "supplier": "Rucher des Lilas", "stock": 10}
                """.formatted(venteId),
                """
                {"venteId": %d, "name": "Pommes Gala", "description": "Pommes Gala croquantes du verger, 1kg", "price": 2.80, "supplier": "Verger Dupont", "stock": 0}
                """.formatted(venteId),
                """
                {"venteId": %d, "name": "Fromage de chèvre", "description": "Fromage frais de chèvre fermier, 200g", "price": 5.60, "supplier": "Chèvrerie du Val", "stock": 8}
                """.formatted(venteId)
        );

        List<Long> productIds = new ArrayList<>();
        for (String productBody : products) {
            Long productId = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(productBody)
                    .when()
                    .post("/api/admin/products")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getLong("data.id");
            productIds.add(productId);
        }
        testContext.setProductIds(productIds);

        // 3. Create time slots via admin endpoint
        List<Long> timeSlotIds = new ArrayList<>();
        String[][] slots = {
                {"10:00", "11:00"},
                {"11:00", "12:00"},
                {"14:00", "15:00"},
                {"15:00", "16:00"}
        };

        for (String[] slot : slots) {
            String timeSlotBody = """
                    {"venteId": %d, "date": "%s", "startTime": "%s", "endTime": "%s", "capacity": 30}
                    """.formatted(venteId, futureDate, slot[0], slot[1]);

            Long timeSlotId = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(timeSlotBody)
                    .when()
                    .post("/api/admin/timeslots")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getLong("data.id");
            timeSlotIds.add(timeSlotId);
        }
        testContext.setTimeSlotIds(timeSlotIds);

        // 4. Activate the vente
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/admin/ventes/" + venteId + "/activate")
                .then()
                .statusCode(200);
    }
}
