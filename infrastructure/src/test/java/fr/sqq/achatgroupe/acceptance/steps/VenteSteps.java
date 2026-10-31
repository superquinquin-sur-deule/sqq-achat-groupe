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

    @Étantdonnéque("une vente existe avec un produit ayant un stock de {int}")
    public void uneVenteExisteAvecUnProduitAyantUnStockDe(int stock) {
        createVenteWithLimitedStock(stock);
    }

    @Étantdonnéque("une vente existe avec un produit à variantes")
    public void uneVenteExisteAvecUnProduitAVariantes() {
        createVenteWithVariants();
    }

    @Étantdonnéque("une vente sans créneau existe avec des produits")
    public void uneVenteSansCreneauExisteAvecDesProduits() {
        createVenteSansCreneau();
    }

    @Étantdonnéque("une vente sans commande existe")
    public void uneVenteSansCommandeExiste() {
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);

        Long emptyVenteId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Vente Vide %d",
                            "description": "Vente sans commande",
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

        testContext.setEmptyVenteId(emptyVenteId);
    }

    private void createVenteWithLimitedStock(int stock) {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);

        Long venteId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Vente Stock Limité %d", "description": "Test stock limité", "startDate": "%s", "endDate": "%s"}
                        """.formatted(System.nanoTime(), startDate, endDate))
                .when()
                .post("/api/admin/ventes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("data.id");

        testContext.setVenteId(venteId);

        var limitedStockJson = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Produit stock limité", "description": "Produit avec stock limité pour test", "tauxTva": 5.50, "supplier": "Fournisseur Test", "category": "Test", "brand": "Test Brand", "variants": [{"label": "", "reference": "TST-001", "prixHt": 5.00, "stock": %d}]}
                        """.formatted(stock))
                .when()
                .post("/api/admin/ventes/" + venteId + "/products")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        testContext.setProductIds(List.of(limitedStockJson.getLong("data.id")));
        testContext.setVariantIds(List.of(limitedStockJson.getLong("data.variants[0].id")));

        String timeSlotBody = """
                {"date": "%s", "startTime": "10:00", "endTime": "11:00", "capacity": 30}
                """.formatted(futureDate);

        Long timeSlotId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(timeSlotBody)
                .when()
                .post("/api/admin/ventes/" + venteId + "/timeslots")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("data.id");

        testContext.setTimeSlotIds(List.of(timeSlotId));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/admin/ventes/" + venteId + "/activate")
                .then()
                .statusCode(200);
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
                {"name": "Tomates bio", "description": "Tomates grappe biologiques cultivées localement", "tauxTva": 5.50, "supplier": "Ferme du Soleil", "category": "Légumes", "brand": "Ferme du Soleil", "importantNote": "Le poids est approximatif et peut varier à la réception", "variants": [{"label": "", "reference": "TOM-001", "prixHt": 3.32, "stock": 25, "colisage": 6}]}
                """,
                """
                {"name": "Pain de campagne", "description": "Pain artisanal au levain naturel, 500g", "tauxTva": 5.50, "supplier": "Boulangerie Martin", "category": "Boulangerie", "brand": "Boulangerie Martin", "variants": [{"label": "", "reference": "PAI-001", "prixHt": 3.98, "stock": 15}]}
                """,
                """
                {"name": "Miel de fleurs", "description": "Miel toutes fleurs récolté en Île-de-France, 250g", "tauxTva": 5.50, "supplier": "Rucher des Lilas", "category": "Épicerie", "brand": "Rucher des Lilas", "variants": [{"label": "", "reference": "MIE-001", "prixHt": 8.44, "stock": 10}]}
                """,
                """
                {"name": "Pommes Gala", "description": "Pommes Gala croquantes du verger, 1kg", "tauxTva": 5.50, "supplier": "Verger Dupont", "category": "Fruits", "brand": "Verger Dupont", "variants": [{"label": "", "reference": "POM-001", "prixHt": 2.65, "stock": 0}]}
                """,
                """
                {"name": "Fromage de chèvre", "description": "Fromage frais de chèvre fermier, 200g", "tauxTva": 5.50, "supplier": "Chèvrerie du Val", "category": "Produits laitiers", "brand": "Chèvrerie du Val", "variants": [{"label": "", "reference": "FRO-001", "prixHt": 5.31, "stock": 8}]}
                """
        );

        List<Long> productIds = new ArrayList<>();
        List<Long> variantIds = new ArrayList<>();
        for (String productBody : products) {
            var json = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(productBody)
                    .when()
                    .post("/api/admin/ventes/" + venteId + "/products")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            productIds.add(json.getLong("data.id"));
            variantIds.add(json.getLong("data.variants[0].id"));
        }
        testContext.setProductIds(productIds);
        testContext.setVariantIds(variantIds);

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
                    {"date": "%s", "startTime": "%s", "endTime": "%s", "capacity": 30}
                    """.formatted(futureDate, slot[0], slot[1]);

            Long timeSlotId = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(timeSlotBody)
                    .when()
                    .post("/api/admin/ventes/" + venteId + "/timeslots")
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

    /**
     * Vente en mode plage de dates : mêmes produits que createVente(), mais aucun créneau —
     * le retrait se fait librement entre pickupStartDate et pickupEndDate.
     */
    private void createVenteSansCreneau() {
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);
        LocalDate pickupStartDate = LocalDate.now().plusDays(30);
        LocalDate pickupEndDate = LocalDate.now().plusDays(33);

        String venteBody = """
                {
                    "name": "Vente Sans Créneau %d",
                    "description": "Vente de test sans créneau",
                    "startDate": "%s",
                    "endDate": "%s",
                    "pickupMode": "DATE_RANGE",
                    "pickupStartDate": "%s",
                    "pickupEndDate": "%s"
                }
                """.formatted(System.nanoTime(), startDate, endDate, pickupStartDate, pickupEndDate);

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
        testContext.setPickupRange(pickupStartDate, pickupEndDate);

        List<String> products = List.of(
                """
                {"name": "Tomates bio", "description": "Tomates grappe biologiques cultivées localement", "tauxTva": 5.50, "supplier": "Ferme du Soleil", "category": "Légumes", "brand": "Ferme du Soleil", "importantNote": "Le poids est approximatif et peut varier à la réception", "variants": [{"label": "", "reference": "TOM-001", "prixHt": 3.32, "stock": 25, "colisage": 6}]}
                """,
                """
                {"name": "Pain de campagne", "description": "Pain artisanal au levain naturel, 500g", "tauxTva": 5.50, "supplier": "Boulangerie Martin", "category": "Boulangerie", "brand": "Boulangerie Martin", "variants": [{"label": "", "reference": "PAI-001", "prixHt": 3.98, "stock": 15}]}
                """
        );

        List<Long> productIds = new ArrayList<>();
        List<Long> variantIds = new ArrayList<>();
        for (String productBody : products) {
            var json = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(productBody)
                    .when()
                    .post("/api/admin/ventes/" + venteId + "/products")
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            productIds.add(json.getLong("data.id"));
            variantIds.add(json.getLong("data.variants[0].id"));
        }
        testContext.setProductIds(productIds);
        testContext.setVariantIds(variantIds);
        testContext.setTimeSlotIds(List.of());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/admin/ventes/" + venteId + "/activate")
                .then()
                .statusCode(200);
    }

    /**
     * Vente contenant un produit simple et un produit à trois variantes (dont une épuisée),
     * pour exercer le sélecteur de déclinaison du catalogue.
     */
    private void createVenteWithVariants() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(60, ChronoUnit.DAYS);

        Long venteId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Vente Variantes %d",
                            "description": "Vente de test avec declinaisons",
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

        // Produit simple : doit continuer à afficher directement le bouton Ajouter.
        var simpleJson = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Couronne de l'Avent", "description": "Couronne artisanale", "tauxTva": 20.00, "supplier": "Pepiniere des Cimes", "category": "Decoration", "brand": "Pepiniere des Cimes", "variants": [{"label": "", "reference": "COU-001", "prixHt": 19.90, "stock": 10}]}
                        """)
                .when()
                .post("/api/admin/ventes/" + venteId + "/products")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        // Produit à variantes : 250cm est épuisé pour vérifier l'option désactivée.
        var variantJson = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Sapin Nordmann", "description": "Sapin coupe frais", "tauxTva": 20.00, "supplier": "Pepiniere des Cimes", "category": "Decoration", "brand": "Pepiniere des Cimes", "variants": [
                            {"label": "150cm", "reference": "SAP-150", "prixHt": 25.00, "stock": 20, "colisage": 5},
                            {"label": "200cm", "reference": "SAP-200", "prixHt": 35.00, "stock": 15, "colisage": 3},
                            {"label": "250cm", "reference": "SAP-250", "prixHt": 50.00, "stock": 0, "colisage": 2}
                        ]}
                        """)
                .when()
                .post("/api/admin/ventes/" + venteId + "/products")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        testContext.setProductIds(List.of(simpleJson.getLong("data.id"), variantJson.getLong("data.id")));
        testContext.setVariantIds(List.of(
                simpleJson.getLong("data.variants[0].id"),
                variantJson.getLong("data.variants[0].id"),
                variantJson.getLong("data.variants[1].id"),
                variantJson.getLong("data.variants[2].id")));

        Long timeSlotId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"date": "%s", "startTime": "10:00", "endTime": "11:00", "capacity": 30}
                        """.formatted(futureDate))
                .when()
                .post("/api/admin/ventes/" + venteId + "/timeslots")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("data.id");

        testContext.setTimeSlotIds(List.of(timeSlotId));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/admin/ventes/" + venteId + "/activate")
                .then()
                .statusCode(200);
    }
}
