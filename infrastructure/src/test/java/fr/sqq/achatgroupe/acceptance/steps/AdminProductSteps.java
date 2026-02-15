package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
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
                .get("/api/admin/products?venteId=" + testContext.venteId());
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
                    "venteId": %d,
                    "name": "%s",
                    "description": "Description test",
                    "price": %s,
                    "supplier": "%s",
                    "stock": %d
                }
                """.formatted(testContext.venteId(), name, price, supplier, stock);

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/admin/products");
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
                .put("/api/admin/products/" + productId);
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
                .get("/api/admin/products?venteId=" + testContext.venteId());
        initialProductCount = listResponse.jsonPath().getList("data").size();

        Long productId = testContext.productIds().get(0);
        response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/admin/products/" + productId);
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
                .get("/api/admin/products?venteId=" + testContext.venteId());
        listResponse.then()
                .statusCode(200)
                .body("data.size()", equalTo(initialProductCount - 1));
    }

    @Quand("je crée un produit admin avec un nom vide")
    public void jeCreeUnProduitAvecUnNomVide() {
        String body = """
                {
                    "venteId": %d,
                    "name": "",
                    "description": "",
                    "price": 3.50,
                    "supplier": "Test",
                    "stock": 10
                }
                """.formatted(testContext.venteId());

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/admin/products");
    }

    @Alors("je reçois une erreur de validation 400")
    public void jeRecoisUneErreurDeValidation() {
        response.then()
                .statusCode(400);
    }
}
