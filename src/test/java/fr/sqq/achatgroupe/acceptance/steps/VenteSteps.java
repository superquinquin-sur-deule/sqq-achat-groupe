package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Étantdonnéque;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class VenteSteps {

    @Inject
    TestContext testContext;

    @Étantdonnéque("une vente existe avec des produits et des créneaux")
    public void uneVenteExisteAvecDesProduitsEtDesCreneaux() {
        createVente();
    }

    private void createVente() {
        LocalDate futureDate = LocalDate.now().plusDays(30);

        String body = """
                {
                    "name": "Vente Test %d",
                    "description": "Vente de test automatisée",
                    "products": [
                        {"name": "Tomates bio", "description": "Tomates grappe biologiques cultivées localement", "price": 3.50, "supplier": "Ferme du Soleil", "stock": 25},
                        {"name": "Pain de campagne", "description": "Pain artisanal au levain naturel, 500g", "price": 4.20, "supplier": "Boulangerie Martin", "stock": 15},
                        {"name": "Miel de fleurs", "description": "Miel toutes fleurs récolté en Île-de-France, 250g", "price": 8.90, "supplier": "Rucher des Lilas", "stock": 10},
                        {"name": "Pommes Gala", "description": "Pommes Gala croquantes du verger, 1kg", "price": 2.80, "supplier": "Verger Dupont", "stock": 0},
                        {"name": "Fromage de chèvre", "description": "Fromage frais de chèvre fermier, 200g", "price": 5.60, "supplier": "Chèvrerie du Val", "stock": 8}
                    ],
                    "timeSlots": [
                        {"date": "%s", "startTime": "10:00", "endTime": "11:00", "capacity": 30},
                        {"date": "%s", "startTime": "11:00", "endTime": "12:00", "capacity": 30},
                        {"date": "%s", "startTime": "14:00", "endTime": "15:00", "capacity": 30},
                        {"date": "%s", "startTime": "15:00", "endTime": "16:00", "capacity": 30}
                    ]
                }
                """.formatted(
                        System.nanoTime(),
                        futureDate, futureDate, futureDate, futureDate
                );

        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/ventes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath();

        testContext.setVenteId(response.getLong("data.id"));
        testContext.setProductIds(response.getList("data.productIds", Long.class));
        testContext.setTimeSlotIds(response.getList("data.timeSlotIds", Long.class));
    }
}
