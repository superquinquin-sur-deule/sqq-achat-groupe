package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand;
import fr.sqq.achatgroupe.application.command.RecordReceptionCommand.ReceptionLineCommand;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.order.Order;
import fr.sqq.achatgroupe.domain.model.order.OrderItem;
import fr.sqq.mediator.Mediator;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BackofficeReceptionSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ProductRepository productRepository;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page réception")
    public void jeNavigueVersLaPageReception() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> {
            if (route.request().url().contains("/receptions")) {
                route.fallback();
            } else {
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("application/json")
                        .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}"));
            }
        });

        page().navigate(PlaywrightHooks.testUrl() + "/admin/reception");
        page().waitForSelector("[data-testid='reception-title'], [data-testid='reception-loading']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
        // Wait for loading to finish
        page().waitForSelector("[data-testid='reception-suppliers'], [data-testid='reception-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le titre réception {string}")
    public void jeVoisLeTitreReception(String title) {
        Locator titleEl = page().locator("[data-testid='reception-title']");
        assertTrue(titleEl.isVisible(), "Le titre de la page réception doit être visible");
        assertTrue(titleEl.textContent().contains(title), "Le titre doit contenir '" + title + "'");
    }

    @Et("je vois la liste des fournisseurs avec leur statut")
    public void jeVoisLaListeDesFournisseursAvecLeurStatut() {
        Locator supplierRows = page().locator("[data-testid='reception-supplier-row']");
        assertTrue(supplierRows.count() >= 1,
                "Il doit y avoir au moins 1 fournisseur, trouvé: " + supplierRows.count());

        // Each supplier should have a name and status badge
        Locator firstRow = supplierRows.first();
        Locator name = firstRow.locator("[data-testid='reception-supplier-name']");
        assertTrue(name.isVisible(), "Le nom du fournisseur doit être visible");
        assertFalse(name.textContent().isBlank(), "Le nom du fournisseur ne doit pas être vide");

        Locator status = firstRow.locator("[data-testid='reception-supplier-status']");
        assertTrue(status.isVisible(), "Le statut du fournisseur doit être visible");
    }

    @Et("je clique sur saisir réception pour le premier fournisseur")
    public void jeCliqueSurSaisirReceptionPourLePremierFournisseur() {
        Locator btn = page().locator("[data-testid='reception-record-btn']").first();
        assertTrue(btn.isVisible(), "Le bouton 'Saisir réception' doit être visible");
        btn.click();
        page().waitForSelector("[data-testid='reception-form-modal']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
    }

    @Alors("je vois le formulaire de réception")
    public void jeVoisLeFormulaireDeReception() {
        Locator modal = page().locator("[data-testid='reception-form-modal']");
        assertTrue(modal.isVisible(), "Le formulaire de réception doit être visible");

        Locator table = page().locator("[data-testid='reception-form-table']");
        assertTrue(table.isVisible(), "Le tableau du formulaire doit être visible");
    }

    @Quand("je valide la réception avec les quantités par défaut")
    public void jeValideLaReceptionAvecLesQuantitesParDefaut() {
        Locator submit = page().locator("[data-testid='reception-form-submit']");
        submit.click();
        // Wait for modal to close
        page().waitForSelector("[data-testid='reception-form-modal']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(10000));
    }

    @Alors("le fournisseur est marqué comme reçu")
    public void leFournisseurEstMarqueCommeRecu() {
        // Wait for status update
        page().waitForTimeout(1000);
        Locator statuses = page().locator("[data-testid='reception-supplier-status']");
        assertTrue(statuses.count() >= 1, "Il doit y avoir des statuts visibles");
        boolean hasCompleted = false;
        for (int i = 0; i < statuses.count(); i++) {
            if (statuses.nth(i).textContent().contains("Reçu")) {
                hasCompleted = true;
                break;
            }
        }
        assertTrue(hasCompleted, "Au moins un fournisseur doit être marqué comme 'Reçu'");
    }

    @Étantdonnéque("toutes les réceptions sont enregistrées avec une rupture sur le premier produit")
    @Transactional
    public void toutesLesReceptionsSontEnregistreesAvecUneRupture() {
        Long venteId = testContext.venteId();
        List<Product> products = productRepository.findAllByVenteId(venteId);
        List<Order> paidOrders = orderRepository.findPaidByVenteId(venteId);

        // Group products by supplier
        Map<String, List<Product>> productsBySupplier = products.stream()
                .collect(Collectors.groupingBy(Product::supplier));

        // Compute ordered quantities per product from paid orders
        Map<Long, Integer> orderedQuantities = new HashMap<>();
        for (Order order : paidOrders) {
            for (OrderItem item : order.items()) {
                orderedQuantities.merge(item.productId(), item.quantity(), Integer::sum);
            }
        }

        boolean firstProduct = true;
        for (Map.Entry<String, List<Product>> entry : productsBySupplier.entrySet()) {
            String supplier = entry.getKey();
            List<ReceptionLineCommand> lines = new java.util.ArrayList<>();

            for (Product product : entry.getValue()) {
                int ordered = orderedQuantities.getOrDefault(product.id(), 0);
                if (ordered == 0) continue;

                int received;
                if (firstProduct && ordered > 0) {
                    // Create a shortage on the first product that has orders
                    received = Math.max(0, ordered - 1);
                    firstProduct = false;
                } else {
                    received = ordered;
                }
                lines.add(new ReceptionLineCommand(product.id(), received));
            }

            if (!lines.isEmpty()) {
                mediator.send(new RecordReceptionCommand(venteId, supplier, lines));
            }
        }
    }

    @Alors("je vois l'aperçu des manques")
    public void jeVoisAPercuDesManques() {
        // Wait for shortage section to appear (requires sequential API calls)
        page().waitForSelector("[data-testid='reception-shortages']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));

        Locator shortages = page().locator("[data-testid='reception-shortages']");
        assertTrue(shortages.isVisible(), "L'aperçu des manques doit être visible");

        page().waitForSelector("[data-testid='shortage-table']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));

        Locator table = page().locator("[data-testid='shortage-table']");
        assertTrue(table.isVisible(), "Le tableau des manques doit être visible");

        Locator rows = page().locator("[data-testid='shortage-row']");
        assertTrue(rows.count() >= 1, "Il doit y avoir au moins 1 produit en manque");
    }

    @Et("je vois le bouton appliquer les ajustements")
    public void jeVoisLeBoutonAppliquerLesAjustements() {
        Locator btn = page().locator("[data-testid='apply-adjustments-btn']");
        assertTrue(btn.isVisible(), "Le bouton 'Appliquer les ajustements' doit être visible");
    }

    @Et("je clique sur modifier pour le premier fournisseur")
    public void jeCliqueSurModifierPourLePremierFournisseur() {
        Locator btn = page().locator("[data-testid='reception-edit-btn']").first();
        assertTrue(btn.isVisible(), "Le bouton 'Modifier' doit être visible");
        btn.click();
        page().waitForSelector("[data-testid='reception-form-modal']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
    }

    @Alors("je vois le formulaire de modification de réception")
    public void jeVoisLeFormulaireDeModificationDeReception() {
        Locator modal = page().locator("[data-testid='reception-form-modal']");
        assertTrue(modal.isVisible(), "Le formulaire de modification doit être visible");

        Locator title = modal.locator("h3");
        assertTrue(title.textContent().contains("Modifier"),
                "Le titre doit contenir 'Modifier', trouvé: " + title.textContent());
    }
}
