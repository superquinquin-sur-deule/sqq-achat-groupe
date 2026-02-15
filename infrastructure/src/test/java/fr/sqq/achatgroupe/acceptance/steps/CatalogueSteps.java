package fr.sqq.achatgroupe.acceptance.steps;

import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.inject.Inject;

import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CatalogueSteps {

    @Inject
    TestContext testContext;

    private Response response;

    @Quand("je consulte la liste des produits")
    public void jeConsulteLaListeDesProduits() {
        response = RestAssured.given()
                .when()
                .get("/api/ventes/" + testContext.venteId() + "/products");
    }

    @Quand("je consulte le détail du premier produit")
    public void jeConsulteLeDetailDuPremierProduit() {
        Long productId = testContext.productIds().get(0);
        response = RestAssured.given()
                .when()
                .get("/api/ventes/" + testContext.venteId() + "/products/" + productId);
    }

    @Quand("je consulte le produit avec l'id {int}")
    public void jeConsulteLeProduitsAvecId(int id) {
        response = RestAssured.given()
                .when()
                .get("/api/ventes/" + testContext.venteId() + "/products/" + id);
    }

    @Alors("je reçois uniquement les produits actifs")
    public void jeRecoisUniquementLesProduitsActifs() {
        response.then()
                .statusCode(200)
                .body("data.size()", greaterThan(0))
                .body("data.size()", equalTo(5));
    }

    @Et("la réponse contient les champs id, name, description, price, supplier, stock")
    public void laReponseContientLesChamps() {
        response.then()
                .body("data[0].id", instanceOf(Number.class))
                .body("data[0].name", instanceOf(String.class))
                .body("data[0].price", instanceOf(Number.class))
                .body("data[0].supplier", instanceOf(String.class))
                .body("data[0]", hasKey("stock"))
                .body("data[0]", hasKey("description"));
    }

    @Et("la réponse contient les métadonnées de pagination")
    public void laReponseContientLesMetadonnees() {
        response.then()
                .body("meta.total", instanceOf(Number.class))
                .body("meta.page", equalTo(1))
                .body("meta.pageSize", instanceOf(Number.class));
    }

    @Alors("je reçois le détail du produit")
    public void jeRecoisLeDetailDuProduit() {
        response.then()
                .statusCode(200);
    }

    @Et("la réponse contient le produit dans un champ data")
    public void laReponseContientLeProduitDansData() {
        Long expectedId = testContext.productIds().get(0);
        response.then()
                .body("data.id", equalTo(expectedId.intValue()))
                .body("data.name", instanceOf(String.class))
                .body("data.price", instanceOf(Number.class))
                .body("data.supplier", instanceOf(String.class))
                .body("data", hasKey("stock"));
    }

    @Alors("je reçois une erreur 404")
    public void jeRecoisUneErreur404() {
        response.then()
                .statusCode(404);
    }

    @Et("la réponse est au format RFC 7807")
    public void laReponseEstAuFormatRfc7807() {
        response.then()
                .contentType("application/problem+json")
                .body("type", notNullValue())
                .body("title", equalTo("Not Found"))
                .body("status", equalTo(404))
                .body("detail", containsString("999999"));
    }

    @Alors("un produit avec un stock de 0 est présent dans la liste")
    public void unProduitAvecStockZeroEstPresent() {
        response.then()
                .statusCode(200)
                .body("data.find { it.stock == 0 }", notNullValue());
    }
}
