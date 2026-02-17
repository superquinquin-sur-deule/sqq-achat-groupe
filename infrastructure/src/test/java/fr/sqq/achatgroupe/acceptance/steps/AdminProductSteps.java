package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AdminProductSteps {

    @Inject
    TestContext testContext;

    private Response response;
    private int initialProductCount;

    @Quand("je liste les produits admin de la vente")
    public void jeListeLesProduitsDeLaVente() {
        response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/products");
    }

    @Alors("je reçois la liste de tous les produits")
    public void jeRecoisLaListeDeTousLesProduits() {
        response.then()
                .statusCode(200)
                .body("data.size()", greaterThan(0));
    }

    @Et("chaque produit contient les champs id, venteId, name, price, supplier, stock et active")
    public void chaqueProduitContientLesChamps() {
        response.then()
                .body("data[0].id", instanceOf(Number.class))
                .body("data[0].venteId", instanceOf(Number.class))
                .body("data[0].name", instanceOf(String.class))
                .body("data[0].price", instanceOf(Number.class))
                .body("data[0].supplier", instanceOf(String.class))
                .body("data[0]", hasKey("stock"))
                .body("data[0]", hasKey("active"));
    }

    @Quand("je crée un produit admin avec le nom {string} au prix de {double} du fournisseur {string} avec un stock de {int}")
    public void jeCreeProduitAdmin(String name, double price, String supplier, int stock) {
        String body = """
                {
                    "name": "%s",
                    "description": "Description test",
                    "price": %s,
                    "supplier": "%s",
                    "stock": %d
                }
                """.formatted(name, price, supplier, stock);

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/admin/ventes/" + testContext.venteId() + "/products");
    }

    @Alors("le produit est créé avec succès")
    public void leProduitEstCreeAvecSucces() {
        response.then()
                .statusCode(200)
                .body("data.id", instanceOf(Number.class));
    }

    @Et("le produit créé a le nom {string}")
    public void leProduitCreeALeNom(String name) {
        response.then()
                .body("data.name", equalTo(name));
    }

    @Et("le produit créé est actif")
    public void leProduitCreeEstActif() {
        response.then()
                .body("data.active", is(true));
    }

    @Quand("je modifie le premier produit avec le nom {string} et le prix {double}")
    public void jeModifieLePremierProduit(String name, double price) {
        Long productId = testContext.productIds().get(0);

        String body = """
                {
                    "name": "%s",
                    "description": "Description modifiée",
                    "price": %s,
                    "supplier": "Ferme du Soleil",
                    "stock": 25,
                    "active": true
                }
                """.formatted(name, price);

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/admin/ventes/" + testContext.venteId() + "/products/" + productId);
    }

    @Alors("le produit est modifié avec succès")
    public void leProduitEstModifieAvecSucces() {
        response.then()
                .statusCode(200)
                .body("data.id", instanceOf(Number.class));
    }

    @Et("le produit modifié a le nom {string}")
    public void leProduitModifieALeNom(String name) {
        response.then()
                .body("data.name", equalTo(name));
    }

    @Quand("je supprime le premier produit")
    public void jeSupprimeLePremierProduit() {
        // First, count current products
        Response listResponse = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/products");
        initialProductCount = listResponse.jsonPath().getList("data").size();

        Long productId = testContext.productIds().get(0);
        response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/admin/ventes/" + testContext.venteId() + "/products/" + productId);
    }

    @Alors("le produit est supprimé avec succès")
    public void leProduitEstSupprime() {
        response.then()
                .statusCode(204);
    }

    @Et("la liste des produits admin a un produit de moins")
    public void laListeAUnProduitDeMoins() {
        Response listResponse = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/products");
        listResponse.then()
                .statusCode(200)
                .body("data.size()", equalTo(initialProductCount - 1));
    }

    @Quand("je crée un produit admin avec un nom vide")
    public void jeCreeUnProduitAvecUnNomVide() {
        String body = """
                {
                    "name": "",
                    "description": "",
                    "price": 3.50,
                    "supplier": "Test",
                    "stock": 10
                }
                """;

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/admin/ventes/" + testContext.venteId() + "/products");
    }

    @Alors("je reçois une erreur de validation 400")
    public void jeRecoisUneErreurDeValidation() {
        response.then()
                .statusCode(400);
    }

    @Étantdonnéque("une commande existe sur la vente")
    public void uneCommandeExisteSurLaVente() {
        Long venteId = testContext.venteId();
        Long productId = testContext.productIds().get(0);
        Long timeSlotId = testContext.timeSlotIds().get(0);

        String body = """
                {
                    "customerFirstName": "Jean",
                    "customerLastName": "Dupont",
                    "email": "jean.dupont@test.fr",
                    "phone": "0612345678",
                    "timeSlotId": %d,
                    "items": [{"productId": %d, "quantity": 1}]
                }
                """.formatted(timeSlotId, productId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/ventes/" + venteId + "/orders")
                .then()
                .statusCode(201);
    }

    @Alors("je reçois une erreur de conflit 409")
    public void jeRecoisUneErreurDeConflit409() {
        response.then()
                .statusCode(409);
    }

    @Quand("je désactive le premier produit")
    public void jeDesactiveLePremierProduit() {
        Long productId = testContext.productIds().get(0);

        Response productResponse = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/ventes/" + testContext.venteId() + "/products");

        var products = productResponse.jsonPath().getList("data");
        var product = productResponse.jsonPath();
        String name = null;
        String description = null;
        Number price = null;
        String supplier = null;
        Number stock = null;

        for (int i = 0; i < products.size(); i++) {
            Number id = productResponse.jsonPath().get("data[" + i + "].id");
            if (id.longValue() == productId) {
                name = productResponse.jsonPath().getString("data[" + i + "].name");
                description = productResponse.jsonPath().getString("data[" + i + "].description");
                price = productResponse.jsonPath().get("data[" + i + "].price");
                supplier = productResponse.jsonPath().getString("data[" + i + "].supplier");
                stock = productResponse.jsonPath().get("data[" + i + "].stock");
                break;
            }
        }

        String body = """
                {
                    "name": "%s",
                    "description": "%s",
                    "price": %s,
                    "supplier": "%s",
                    "stock": %s,
                    "active": false
                }
                """.formatted(name, description, price, supplier, stock);

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/admin/ventes/" + testContext.venteId() + "/products/" + productId);
    }

    @Quand("je supprime la vente")
    public void jeSupprimeLaVente() {
        response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/admin/ventes/" + testContext.venteId());
    }
}
