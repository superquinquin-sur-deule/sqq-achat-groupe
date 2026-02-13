package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase;
import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase.CreateOrderCommand;
import fr.sqq.achatgroupe.application.port.in.CreateOrderUseCase.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class BackofficeSupplierOrderSteps {

    @Inject
    TestContext testContext;

    @Inject
    CreateOrderUseCase createOrderUseCase;

    @Inject
    OrderRepository orderRepository;

    private Long emptyVenteId;
    private Download lastDownload;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Étantdonnéque("il existe des commandes payées pour le bon fournisseur")
    @Transactional
    public void ilExisteDesCommandesPayeesPourLeBonFournisseur() {
        Long venteId = testContext.venteId();
        Long product1Id = testContext.productIds().get(0);
        Long product2Id = testContext.productIds().get(1);
        Long product3Id = testContext.productIds().get(2);
        Long timeSlotId = testContext.timeSlotIds().get(0);

        // Commande 1 : 3x produit1 + 2x produit2
        Order order1 = createOrderUseCase.execute(new CreateOrderCommand(
                venteId, "Alice Durand", "alice@test.fr", "0601020304", timeSlotId,
                List.of(new OrderItemCommand(product1Id, 3), new OrderItemCommand(product2Id, 2))
        ));
        order1.markAsPaid();
        orderRepository.save(order1);

        // Commande 2 : 1x produit1 + 4x produit3
        Order order2 = createOrderUseCase.execute(new CreateOrderCommand(
                venteId, "Bob Martin", "bob@test.fr", "0605060708", timeSlotId,
                List.of(new OrderItemCommand(product1Id, 1), new OrderItemCommand(product3Id, 4))
        ));
        order2.markAsPaid();
        orderRepository.save(order2);
    }

    @Quand("je navigue vers la page bon fournisseur")
    public void jeNavigueVersLaPageBonFournisseur() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}]}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/supplier-order");
        page().waitForSelector("[data-testid='supplier-order-table'], [data-testid='supplier-order-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le titre bon fournisseur {string}")
    public void jeVoisLeTitreBonFournisseur(String title) {
        Locator titleEl = page().locator("[data-testid='supplier-order-title']");
        assertTrue(titleEl.isVisible(), "Le titre du bon fournisseur doit être visible");
        assertTrue(titleEl.textContent().contains(title), "Le titre doit contenir '" + title + "'");
    }

    @Et("je vois les produits groupés par fournisseur avec les quantités totales")
    public void jeVoisLesProduitsGroupesParFournisseurAvecQuantites() {
        Locator table = page().locator("[data-testid='supplier-order-table']");
        assertTrue(table.isVisible(), "Le tableau doit être visible");

        Locator groups = page().locator("[data-testid='supplier-group']");
        assertTrue(groups.count() >= 2, "Il doit y avoir au moins 2 groupes fournisseur, trouvé: " + groups.count());

        Locator headers = page().locator("[data-testid='supplier-group-header']");
        assertTrue(headers.count() >= 2, "Il doit y avoir au moins 2 en-têtes fournisseur");

        Locator rows = page().locator("[data-testid='supplier-order-row']");
        assertTrue(rows.count() >= 3, "Il doit y avoir au moins 3 lignes produit, trouvé: " + rows.count());

        // Vérifier qu'au moins une quantité est affichée
        String firstRowText = rows.first().textContent();
        assertFalse(firstRowText.isBlank(), "La première ligne ne doit pas être vide");
    }

    @Alors("je vois le bouton imprimer du bon fournisseur")
    public void jeVoisLeBoutonImprimerDuBonFournisseur() {
        Locator btn = page().locator("[data-testid='supplier-order-print-btn']");
        assertTrue(btn.isVisible(), "Le bouton Imprimer doit être visible");
        assertTrue(btn.textContent().contains("Imprimer"), "Le bouton doit contenir 'Imprimer'");
    }

    @Et("je vois le bouton exporter CSV du bon fournisseur")
    public void jeVoisLeBoutonExporterCsvDuBonFournisseur() {
        Locator btn = page().locator("[data-testid='supplier-order-export-btn']");
        assertTrue(btn.isVisible(), "Le bouton Exporter CSV doit être visible");
        assertTrue(btn.textContent().contains("Exporter CSV"), "Le bouton doit contenir 'Exporter CSV'");
    }

    @Et("je clique sur exporter CSV du bon fournisseur")
    public void jeCliqueSurExporterCsvDuBonFournisseur() {
        lastDownload = page().waitForDownload(() -> {
            page().locator("[data-testid='supplier-order-export-btn']").click();
        });
    }

    @Alors("un fichier CSV bon fournisseur est téléchargé")
    public void unFichierCsvBonFournisseurEstTelecharge() {
        assertNotNull(lastDownload, "Un téléchargement doit avoir été déclenché");
        String filename = lastDownload.suggestedFilename();
        assertTrue(filename.startsWith("bon-fournisseur-"), "Le fichier doit commencer par 'bon-fournisseur-'");
        assertTrue(filename.endsWith(".csv"), "Le fichier doit se terminer par '.csv'");
    }

    @Étantdonnéque("une vente sans commande pour le bon fournisseur")
    public void uneVenteSansCommandePourLeBonFournisseur() {
        LocalDate futureDate = LocalDate.now().plusDays(30);

        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Vente Vide %d",
                            "description": "Vente sans commande",
                            "products": [
                                {"name": "Produit X", "description": "Desc", "price": 5.00, "supplier": "Fournisseur X", "stock": 10}
                            ],
                            "timeSlots": [
                                {"date": "%s", "startTime": "10:00", "endTime": "11:00", "capacity": 10}
                            ]
                        }
                        """.formatted(System.nanoTime(), futureDate))
                .when()
                .post("/api/ventes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath();

        emptyVenteId = response.getLong("data.id");
    }

    @Quand("je navigue vers la page bon fournisseur sans commande")
    public void jeNavigueVersLaPageBonFournisseurSansCommande() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + emptyVenteId + ",\"name\":\"Vente Vide\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}]}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/supplier-order");
        page().waitForSelector("[data-testid='supplier-order-table'], [data-testid='supplier-order-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le message vide bon fournisseur {string}")
    public void jeVoisLeMessageVideBonFournisseur(String message) {
        Locator empty = page().locator("[data-testid='supplier-order-empty']");
        assertTrue(empty.isVisible(), "L'état vide doit être visible");
        assertTrue(empty.textContent().contains(message), "Le message doit contenir '" + message + "'");
    }
}
