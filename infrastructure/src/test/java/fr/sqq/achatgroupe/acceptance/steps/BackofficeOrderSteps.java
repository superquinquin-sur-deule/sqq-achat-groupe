package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.mediator.Mediator;
import fr.sqq.achatgroupe.application.port.out.OrderRepository;
import fr.sqq.achatgroupe.domain.model.order.Order;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class BackofficeOrderSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    @Inject
    OrderRepository orderRepository;

    private String selectedSlotLabel;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Étantdonnéque("il existe des commandes payées pour cette vente")
    @Transactional
    public void ilExisteDesCommandesPayeesPourCetteVente() {
        Long venteId = testContext.venteId();
        Long productId = testContext.productIds().get(0);
        Long timeSlotId1 = testContext.timeSlotIds().get(0);
        Long timeSlotId2 = testContext.timeSlotIds().get(1);

        // Créer deux commandes PAID sur des créneaux différents
        Order order1 = mediator.send(new CreateOrderCommand(
                venteId, "Marie Dupont", "marie@exemple.fr", "0612345678", timeSlotId1,
                List.of(new OrderItemCommand(productId, 2))
        ));
        order1.markAsPaid();
        orderRepository.save(order1);

        Order order2 = mediator.send(new CreateOrderCommand(
                venteId, "Jean Martin", "jean@exemple.fr", "0698765432", timeSlotId2,
                List.of(new OrderItemCommand(productId, 1))
        ));
        order2.markAsPaid();
        orderRepository.save(order2);
    }

    @Quand("je navigue vers la page backoffice commandes")
    public void jeNavigueVersLaPageBackofficeCommandes() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        // Intercepter l'API admin ventes pour ne retourner que la vente du test courant
        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/orders");
        page().waitForSelector("[data-testid='backoffice-orders-table'], [data-testid='empty-state']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois la sidenav d'administration")
    public void jeVoisLaSidenavDAdministration() {
        Locator sidenav = page().locator("[data-testid='admin-sidenav']");
        assertTrue(sidenav.isVisible(), "La sidenav d'administration doit être visible");
    }

    @Et("la sidenav contient les sections Administration et Back-office avec tous les liens")
    public void laSidenavContientLesSectionsAvecTousLesLiens() {
        Locator sidenav = page().locator("[data-testid='admin-sidenav']");
        // Administration links
        assertTrue(sidenav.locator("[data-testid='sidenav-dashboard']").isVisible(), "Le lien Dashboard doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-products']").isVisible(), "Le lien Produits doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-timeslots']").isVisible(), "Le lien Créneaux doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-ventes']").isVisible(), "Le lien Ventes doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-vente-selector']").isVisible(), "Le sélecteur de vente doit être visible");
        // Back-office links
        assertTrue(sidenav.locator("[data-testid='sidenav-orders']").isVisible(), "Le lien Commandes doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-supplier']").isVisible(), "Le lien Bon fournisseur doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-preparation']").isVisible(), "Le lien Préparation doit être visible");
        assertTrue(sidenav.locator("[data-testid='sidenav-distribution']").isVisible(), "Le lien Distribution doit être visible");
    }

    @Alors("je vois le tableau des commandes backoffice")
    public void jeVoisLeTableauDesCommandesBackoffice() {
        Locator table = page().locator("[data-testid='backoffice-orders-table']");
        assertTrue(table.isVisible(), "Le tableau des commandes doit être visible");
    }

    @Et("chaque ligne affiche numéro, nom, email, créneau, montant et statut")
    public void chaqueLigneAfficheLesDonnees() {
        Locator rows = page().locator("[data-testid='backoffice-order-row']");
        assertTrue(rows.count() >= 2, "Il doit y avoir au moins 2 commandes, trouvé: " + rows.count());

        Locator firstRow = rows.first();
        Locator cells = firstRow.locator("td");
        assertEquals(6, cells.count(), "Chaque ligne doit avoir 6 colonnes");
        // Colonne 1 : numéro (lien)
        assertFalse(cells.nth(0).textContent().isBlank(), "Le numéro ne doit pas être vide");
        // Colonne 2 : nom
        assertFalse(cells.nth(1).textContent().isBlank(), "Le nom ne doit pas être vide");
        // Colonne 3 : email
        assertTrue(cells.nth(2).textContent().contains("@"), "L'email doit contenir '@'");
        // Colonne 4 : créneau
        assertFalse(cells.nth(3).textContent().isBlank(), "Le créneau ne doit pas être vide");
        // Colonne 5 : montant
        assertTrue(cells.nth(4).textContent().contains("€"), "Le montant doit contenir '€'");
        // Colonne 6 : statut
        String statusText = cells.nth(5).textContent();
        assertTrue(statusText.contains("Payé") || statusText.contains("Récupéré"),
                "Le statut doit être 'Payé' ou 'Récupéré', got: " + statusText);
    }

    @Et("les badges de statut sont colorés correctement")
    public void lesBadgesDeStatutSontColoresCorrectement() {
        Locator badges = page().locator("[data-testid='backoffice-order-status'] span");
        assertTrue(badges.count() > 0, "Il doit y avoir des badges de statut");
        Locator firstBadge = badges.first();
        String text = firstBadge.textContent();
        assertTrue(text.contains("Payé") || text.contains("Récupéré"),
                "Le badge doit afficher 'Payé' ou 'Récupéré', got: " + text);
    }

    @Et("je saisis {string} dans le champ de recherche backoffice")
    public void jeSaisisDansLeChampDeRecherche(String query) {
        page().locator("[data-testid='backoffice-search-input']").fill(query);
        // Attendre le debounce (300ms) + la requête API + le re-rendu
        page().waitForTimeout(1500);
    }

    @Alors("seules les commandes contenant {string} sont affichées")
    public void seulesLesCommandesContenant(String name) {
        Locator rows = page().locator("[data-testid='backoffice-order-row']");
        assertTrue(rows.count() > 0, "Il doit y avoir des résultats");
        for (int i = 0; i < rows.count(); i++) {
            String text = rows.nth(i).textContent().toLowerCase();
            assertTrue(text.contains(name.toLowerCase()),
                    "La ligne doit contenir '" + name + "', got: " + text);
        }
    }

    @Et("je sélectionne un créneau dans le filtre backoffice")
    public void jeSelectionneUnCreneauDansLeFiltre() {
        Locator filter = page().locator("[data-testid='backoffice-slot-filter']");
        // Sélectionner la première option non vide (premier créneau)
        Locator options = filter.locator("option");
        assertTrue(options.count() > 1, "Il doit y avoir au moins un créneau dans le filtre");
        String firstSlotValue = options.nth(1).getAttribute("value");
        selectedSlotLabel = options.nth(1).textContent().trim();
        filter.selectOption(firstSlotValue);
        page().waitForTimeout(500);
    }

    @Alors("seules les commandes de ce créneau sont affichées")
    public void seulesLesCommandesDeCeCreneau() {
        Locator rows = page().locator("[data-testid='backoffice-order-row']");
        assertTrue(rows.count() > 0, "Il doit y avoir des résultats pour ce créneau");
        for (int i = 0; i < rows.count(); i++) {
            String text = rows.nth(i).textContent();
            assertTrue(text.contains(selectedSlotLabel),
                    "La ligne doit contenir le créneau '" + selectedSlotLabel + "', got: " + text);
        }
    }

    @Et("je clique sur une commande dans la liste backoffice")
    public void jeCliqueSurUneCommandeDansLaListe() {
        Locator firstRow = page().locator("[data-testid='backoffice-order-row']").first();
        firstRow.locator("a").first().click();
        page().waitForSelector("[data-testid='order-detail-customer']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("je vois les coordonnées du client")
    public void jeVoisLesCoordonneesDuClient() {
        Locator customer = page().locator("[data-testid='order-detail-customer']");
        assertTrue(customer.isVisible(), "La section coordonnées doit être visible");
        String text = customer.textContent();
        assertTrue(text.contains("Nom"), "La section doit contenir 'Nom'");
        assertTrue(text.contains("Email"), "La section doit contenir 'Email'");
        assertTrue(text.contains("Téléphone"), "La section doit contenir 'Téléphone'");
    }

    @Et("je vois le créneau de retrait dans le détail backoffice")
    public void jeVoisLeCreneauDeRetraitDansLeDetailBackoffice() {
        Locator timeslot = page().locator("[data-testid='order-detail-timeslot']");
        assertTrue(timeslot.isVisible(), "La section créneau doit être visible");
    }

    @Et("je vois le tableau des produits avec quantités et prix")
    public void jeVoisLeTableauDesProduits() {
        Locator items = page().locator("[data-testid='order-detail-items']");
        assertTrue(items.isVisible(), "Le tableau des produits doit être visible");
        String headerText = items.locator("thead").textContent();
        assertTrue(headerText.contains("Produit"), "Le tableau doit avoir la colonne Produit");
        assertTrue(headerText.contains("Quantité"), "Le tableau doit avoir la colonne Quantité");
        assertTrue(headerText.contains("Prix unitaire"), "Le tableau doit avoir la colonne Prix unitaire");
    }

    @Et("je vois le total de la commande")
    public void jeVoisLeTotalDeLaCommande() {
        Locator total = page().locator("[data-testid='order-detail-total']");
        assertTrue(total.isVisible(), "Le total doit être visible");
        String text = total.textContent();
        assertTrue(text.contains("Total"), "La section doit contenir 'Total'");
        assertTrue(text.contains("€"), "Le total doit afficher un montant en euros");
    }
}
