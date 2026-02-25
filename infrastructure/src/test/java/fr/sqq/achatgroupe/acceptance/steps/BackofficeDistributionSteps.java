package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class BackofficeDistributionSteps {

    @Inject
    TestContext testContext;

    private String selectedSlotText;
    private String pickedUpCustomerName;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page distribution")
    public void jeNavigueVersLaPageDistribution() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        Long venteId = testContext.venteId();
        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + venteId + ",\"name\":\"Vente Test\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/distribution");
        page().waitForSelector("[data-testid='distribution-order-row'], [data-testid='distribution-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le bouton imprimer PDF distribution")
    public void jeVoisLeBoutonImprimerPDFDistribution() {
        Locator printBtn = page().locator("[data-testid='distribution-print-btn']");
        assertTrue(printBtn.isVisible(), "Le bouton 'Imprimer PDF' doit être visible");
        assertTrue(printBtn.textContent().contains("Imprimer PDF"),
                "Le bouton doit contenir le texte 'Imprimer PDF'");
    }

    @Alors("je vois le titre distribution {string}")
    public void jeVoisLeTitreDistribution(String title) {
        Locator titleEl = page().locator("[data-testid='distribution-title']");
        assertTrue(titleEl.isVisible(), "Le titre de la page distribution doit être visible");
        assertTrue(titleEl.textContent().contains(title), "Le titre doit contenir '" + title + "'");
    }

    @Et("je vois les boutons de filtre par créneau")
    public void jeVoisLesBoutonsDeFiltreParcreneau() {
        Locator filter = page().locator("[data-testid='distribution-slot-filter']");
        assertTrue(filter.isVisible(), "Le conteneur de filtre par créneau doit être visible");

        Locator allBtn = page().locator("[data-testid='distribution-slot-all-btn']");
        assertTrue(allBtn.isVisible(), "Le bouton 'Toutes' doit être visible");

        Locator slotBtns = page().locator("[data-testid='distribution-slot-btn']");
        assertTrue(slotBtns.count() >= 2, "Il doit y avoir au moins 2 boutons de créneau, trouvé: " + slotBtns.count());

        Locator unpickedBtn = page().locator("[data-testid='distribution-unpicked-btn']");
        assertTrue(unpickedBtn.isVisible(), "Le bouton 'Non récupérées' doit être visible");
    }

    @Et("je vois toutes les commandes avec leur statut")
    public void jeVoisToutesLesCommandesAvecLeurStatut() {
        Locator rows = page().locator("[data-testid='distribution-order-row']");
        assertTrue(rows.count() >= 4, "Il doit y avoir au moins 4 commandes, trouvé: " + rows.count());

        // Vérifier que les noms apparaissent
        String allText = rows.allTextContents().toString();
        assertTrue(allText.contains("Durand Alice"), "Durand Alice doit apparaître");
        assertTrue(allText.contains("Martin Bob"), "Martin Bob doit apparaître");
        assertTrue(allText.contains("Petit Claire"), "Petit Claire doit apparaître");
        assertTrue(allText.contains("Lopez Diana"), "Lopez Diana doit apparaître");

        // Vérifier les badges de statut (mix PAID et PICKED_UP)
        Locator statuses = page().locator("[data-testid='distribution-order-status']");
        assertTrue(statuses.count() >= 4, "Il doit y avoir au moins 4 badges de statut");
    }

    @Et("je clique sur un bouton de créneau dans le filtre distribution")
    public void jeCliqueSurUnBoutonDeCreneauDansLeFiltreDistribution() {
        Locator slotBtns = page().locator("[data-testid='distribution-slot-btn']");
        assertTrue(slotBtns.count() >= 1, "Il doit y avoir au moins 1 bouton de créneau");
        selectedSlotText = slotBtns.first().textContent().trim();
        slotBtns.first().click();
    }

    @Alors("seules les commandes du créneau sélectionné sont affichées dans la distribution")
    public void seulesLesCommandesDuCreneauSelectionneSontAfficheesDansLaDistribution() {
        Locator rows = page().locator("[data-testid='distribution-order-row']");
        int count = rows.count();
        assertTrue(count >= 1, "Il doit y avoir au moins 1 commande pour le créneau sélectionné");
        // Le nombre affiché doit être inférieur au total (4 commandes au setup)
        assertTrue(count < 4, "Le filtrage doit réduire le nombre de commandes, trouvé: " + count);

        Locator orderCount = page().locator("[data-testid='distribution-order-count']");
        assertTrue(orderCount.isVisible(), "Le compteur de commandes doit être visible");
        assertTrue(orderCount.textContent().contains(String.valueOf(count)),
                "Le compteur doit afficher le bon nombre de commandes filtrées");
    }

    @Et("je saisis {string} dans le champ de recherche distribution")
    public void jeSaisisDansLeChampDeRechercheDistribution(String query) {
        Locator search = page().locator("[data-testid='distribution-search']");
        assertTrue(search.isVisible(), "Le champ de recherche doit être visible");
        search.fill(query);
        // Laisser le temps au computed de se mettre à jour
        page().waitForTimeout(300);
    }

    @Alors("seules les commandes correspondant au nom {string} sont affichées dans la distribution")
    public void seulesLesCommandesCorrespondantAuNomSontAfficheesDansLaDistribution(String name) {
        Locator rows = page().locator("[data-testid='distribution-order-row']");
        assertTrue(rows.count() >= 1, "Il doit y avoir au moins 1 commande correspondant à '" + name + "'");

        // Toutes les commandes affichées doivent contenir le nom recherché
        for (int i = 0; i < rows.count(); i++) {
            Locator nameEl = rows.nth(i).locator("[data-testid='distribution-order-name']");
            assertTrue(nameEl.textContent().toLowerCase().contains(name.toLowerCase()),
                    "La commande doit contenir le nom '" + name + "'");
        }
    }

    @Et("je clique sur \"Marquer récupéré\" pour la première commande payée")
    public void jeCliqueSurMarquerRecuperePourLaPremiereCommandePayee() {
        Locator pickupBtns = page().locator("[data-testid='distribution-pickup-btn']");
        assertTrue(pickupBtns.count() >= 1, "Il doit y avoir au moins 1 bouton 'Marquer récupéré'");

        // Mémoriser le nom du coopérateur de la première commande avec un bouton pickup
        Locator firstRow = pickupBtns.first().locator("xpath=ancestor::div[@data-testid='distribution-order-row']");
        pickedUpCustomerName = firstRow.locator("[data-testid='distribution-order-name']").textContent().trim();

        // Compter les boutons avant le clic
        int countBefore = pickupBtns.count();
        pickupBtns.first().click();

        // Attendre que le bouton disparaisse (preuve que l'optimistic update a eu lieu)
        page().waitForFunction(
                "count => document.querySelectorAll('[data-testid=\"distribution-pickup-btn\"]').length < count",
                countBefore,
                new Page.WaitForFunctionOptions().setTimeout(5000));
    }

    private Locator findOrderRowByName(String name) {
        Locator rows = page().locator("[data-testid='distribution-order-row']");
        for (int i = 0; i < rows.count(); i++) {
            Locator nameEl = rows.nth(i).locator("[data-testid='distribution-order-name']");
            if (nameEl.textContent().trim().equals(name)) {
                return rows.nth(i);
            }
        }
        fail("Aucune ligne trouvée pour le coopérateur '" + name + "'");
        return null;
    }

    @Alors("le statut de la commande distribution passe à {string}")
    public void leStatutDeLaCommandeDistributionPasseA(String expectedStatus) {
        Locator row = findOrderRowByName(pickedUpCustomerName);
        Locator status = row.locator("[data-testid='distribution-order-status']");
        assertTrue(status.isVisible(), "Le badge de statut doit être visible");
        assertTrue(status.textContent().contains(expectedStatus),
                "Le statut doit contenir '" + expectedStatus + "', trouvé: '" + status.textContent() + "'");
    }

    @Et("le bouton \"Marquer récupéré\" disparaît pour cette commande distribution")
    public void leBoutonMarquerRecupereDisparaitPourCetteCommandeDistribution() {
        Locator row = findOrderRowByName(pickedUpCustomerName);
        Locator btn = row.locator("[data-testid='distribution-pickup-btn']");
        assertEquals(0, btn.count(), "Le bouton 'Marquer récupéré' ne doit plus être visible pour cette commande");
    }

    @Et("je clique sur le bouton \"Non récupérées\" dans la distribution")
    public void jeCliqueSurLeBoutonNonRecupereesDansLaDistribution() {
        Locator unpickedBtn = page().locator("[data-testid='distribution-unpicked-btn']");
        assertTrue(unpickedBtn.isVisible(), "Le bouton 'Non récupérées' doit être visible");
        unpickedBtn.click();
        page().waitForTimeout(300);
    }

    @Alors("seules les commandes en statut {string} sont affichées dans la distribution")
    public void seulesLesCommandesEnStatutSontAfficheesDansLaDistribution(String expectedStatus) {
        Locator rows = page().locator("[data-testid='distribution-order-row']");
        assertTrue(rows.count() >= 1, "Il doit y avoir au moins 1 commande en statut '" + expectedStatus + "'");

        // Tous les statuts affichés doivent être le statut attendu
        for (int i = 0; i < rows.count(); i++) {
            Locator status = rows.nth(i).locator("[data-testid='distribution-order-status']");
            assertTrue(status.textContent().contains(expectedStatus),
                    "Toutes les commandes doivent avoir le statut '" + expectedStatus + "', trouvé: '" + status.textContent() + "'");
        }

        // Vérifier que les commandes PICKED_UP sont bien exclues
        String allText = rows.allTextContents().toString();
        assertFalse(allText.contains("Lopez Diana"),
                "Lopez Diana (PICKED_UP) ne doit PAS apparaître dans le filtre 'Non récupérées'");
    }

    @Quand("je navigue vers la page distribution sans commande")
    public void jeNavigueVersLaPageDistributionSansCommande() {
        Long emptyVenteId = testContext.emptyVenteId();

        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Logistique\",\"email\":\"logistique@test.fr\"}}")));

        page().route("**/api/admin/ventes", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":[{\"id\":" + emptyVenteId + ",\"name\":\"Vente Vide\",\"description\":\"Test\",\"status\":\"ACTIVE\",\"startDate\":null,\"endDate\":null,\"createdAt\":\"2026-01-01T00:00:00Z\"}],\"pageInfo\":{\"endCursor\":null,\"hasNext\":false}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/distribution");
        page().waitForSelector("[data-testid='distribution-order-row'], [data-testid='distribution-empty']",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois le message vide distribution {string}")
    public void jeVoisLeMessageVideDistribution(String message) {
        Locator empty = page().locator("[data-testid='distribution-empty']");
        assertTrue(empty.isVisible(), "L'état vide doit être visible");
        assertTrue(empty.textContent().contains(message), "Le message doit contenir '" + message + "'");
    }
}
