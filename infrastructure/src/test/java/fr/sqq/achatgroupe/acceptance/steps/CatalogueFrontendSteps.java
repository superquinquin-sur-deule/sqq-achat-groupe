package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CatalogueFrontendSteps {

    @Inject
    TestContext testContext;

    private String venteUrl() {
        return PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId();
    }

    @Quand("j'accède à la page d'accueil")
    public void jAccedeALaPageDAccueil() {
        PlaywrightHooks.page().navigate(venteUrl());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Quand("j'accède à la page d'accueil en mode mobile")
    public void jAccedeEnModeMobile() {
        PlaywrightHooks.page().setViewportSize(375, 812);
        PlaywrightHooks.page().navigate(venteUrl());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Quand("j'accède à la page d'accueil en mode desktop")
    public void jAccedeEnModeDesktop() {
        PlaywrightHooks.page().setViewportSize(1280, 800);
        PlaywrightHooks.page().navigate(venteUrl());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Quand("la page d'accueil est en cours de chargement")
    public void laPageEstEnCoursDeChargement() {
        // Delay API response in background thread to keep skeleton screens visible
        // without blocking the Playwright message processing thread
        PlaywrightHooks.page().route("**/api/ventes/*/products", route -> {
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                route.resume();
            }).start();
        });
        PlaywrightHooks.page().navigate(venteUrl());
    }

    @Alors("je vois la bannière SVG")
    public void jeVoisLaBanniereSVG() {
        Locator banner = PlaywrightHooks.page().locator("[data-testid='svg-banner']");
        assertTrue(banner.isVisible(), "La bannière SVG doit être visible");
    }

    @Et("je vois le nom de la vente dans le header")
    public void jeVoisLeNomDeLaVenteDansLeHeader() {
        Locator header = PlaywrightHooks.page().locator("[data-testid='vente-header']");
        assertTrue(header.isVisible(), "Le header de vente doit être visible");
        assertFalse(header.textContent().isBlank(), "Le nom de la vente ne doit pas être vide");
    }

    @Et("je vois une grille de cartes produit en dessous du bandeau")
    public void jeVoisUneGrilleDeCartesProduit() {
        Locator grid = PlaywrightHooks.page().locator("[data-testid='product-grid']");
        assertTrue(grid.isVisible(), "La grille de produits doit être visible");
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']");
        assertTrue(cards.count() > 0, "Il doit y avoir au moins une carte produit");
    }

    @Alors("chaque carte affiche le nom du produit")
    public void chaqueCarteAfficheLeNom() {
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']");
        assertTrue(cards.count() > 0, "Il doit y avoir des cartes produit");
        for (int i = 0; i < cards.count(); i++) {
            Locator name = cards.nth(i).locator("h3");
            assertTrue(name.isVisible(), "Le nom du produit doit être visible (carte " + i + ")");
            assertFalse(name.textContent().isBlank(), "Le nom du produit ne doit pas être vide");
        }
    }

    @Et("chaque carte affiche la description du produit")
    public void chaqueCarteAfficheLaDescription() {
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']");
        for (int i = 0; i < cards.count(); i++) {
            Locator description = cards.nth(i).locator("[data-testid='product-description']");
            assertTrue(description.isVisible(), "La description doit être visible (carte " + i + ")");
            assertFalse(description.textContent().isBlank(), "La description ne doit pas être vide");
        }
    }

    @Et("chaque carte affiche le prix")
    public void chaqueCarteAfficheLePrix() {
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']");
        for (int i = 0; i < cards.count(); i++) {
            Locator price = cards.nth(i).locator("[data-testid='product-price']");
            assertTrue(price.isVisible(), "Le prix doit être visible (carte " + i + ")");
        }
    }

    @Et("chaque carte affiche la marque")
    public void chaqueCarteAfficheLaMarque() {
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']");
        for (int i = 0; i < cards.count(); i++) {
            Locator brand = cards.nth(i).locator("[data-testid='product-brand']");
            assertTrue(brand.isVisible(), "La marque doit être visible (carte " + i + ")");
            assertFalse(brand.textContent().isBlank(), "La marque ne doit pas être vide");
        }
    }

    @Et("chaque carte affiche un bouton Ajouter")
    public void chaqueCarteAfficheUnBoutonAjouter() {
        Locator cards = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])");
        for (int i = 0; i < cards.count(); i++) {
            Locator button = cards.nth(i).locator("[data-testid='add-button']");
            assertTrue(button.isVisible(), "Le bouton Ajouter doit être visible (carte " + i + ")");
        }
    }

    @Alors("la carte du produit épuisé est grisée")
    public void laCarteDuProduitEpuiseEstGrisee() {
        Locator exhaustedCards = PlaywrightHooks.page().locator("[data-testid='product-card'][data-exhausted='true']");
        assertTrue(exhaustedCards.count() > 0, "Il doit y avoir au moins un produit épuisé");
        for (int i = 0; i < exhaustedCards.count(); i++) {
            String classes = exhaustedCards.nth(i).getAttribute("class");
            assertNotNull(classes);
            assertTrue(classes.contains("opacity-50"), "La carte épuisée doit avoir une opacité réduite");
        }
    }

    @Et("la carte du produit épuisé affiche la mention Épuisé")
    public void laCarteAfficheLaMentionEpuise() {
        Locator exhaustedCards = PlaywrightHooks.page().locator("[data-testid='product-card'][data-exhausted='true']");
        for (int i = 0; i < exhaustedCards.count(); i++) {
            Locator badge = exhaustedCards.nth(i).locator("[data-testid='exhausted-badge']");
            assertTrue(badge.isVisible(), "Le badge Épuisé doit être visible");
            assertTrue(badge.textContent().contains("Épuisé"), "Le badge doit contenir 'Épuisé'");
        }
    }

    @Et("le bouton Ajouter du produit épuisé est désactivé")
    public void leBoutonAjouterEstDesactive() {
        Locator exhaustedCards = PlaywrightHooks.page().locator("[data-testid='product-card'][data-exhausted='true']");
        for (int i = 0; i < exhaustedCards.count(); i++) {
            Locator button = exhaustedCards.nth(i).locator("[data-testid='add-button']");
            assertTrue(button.isDisabled(), "Le bouton Ajouter doit être désactivé pour un produit épuisé");
        }
    }

    @Alors("je vois les créneaux de retrait disponibles")
    public void jeVoisLesCreneauxDeRetraitDisponibles() {
        Locator banner = PlaywrightHooks.page().locator("[data-testid='timeslot-banner']");
        assertTrue(banner.isVisible(), "Le bandeau des créneaux de retrait doit être visible");
        String text = banner.textContent();
        assertNotNull(text);
        assertTrue(text.contains("Créneaux de retrait"), "Le bandeau doit contenir le titre 'Créneaux de retrait'");
        assertTrue(text.contains("10h00"), "Le bandeau doit afficher un horaire de créneau");
    }

    @Alors("les cartes s'affichent en 1 colonne")
    public void lesCartesEnUneColonne() {
        Locator grid = PlaywrightHooks.page().locator("[data-testid='product-grid'] .grid").first();
        String gridTemplateColumns = grid.evaluate(
                "el => window.getComputedStyle(el).getPropertyValue('grid-template-columns')"
        ).toString();
        int columnCount = gridTemplateColumns.trim().split("\\s+").length;
        assertEquals(1, columnCount, "Mobile doit afficher 1 colonne, got: " + gridTemplateColumns);
    }

    @Alors("les cartes s'affichent en 3 colonnes")
    public void lesCartesEnTroisColonnes() {
        Locator grid = PlaywrightHooks.page().locator("[data-testid='product-grid'] .grid").first();
        String gridTemplateColumns = grid.evaluate(
                "el => window.getComputedStyle(el).getPropertyValue('grid-template-columns')"
        ).toString();
        int columnCount = gridTemplateColumns.trim().split("\\s+").length;
        assertEquals(3, columnCount, "Desktop doit afficher 3 colonnes, got: " + gridTemplateColumns);
    }

    @Alors("les produits sont groupés par rayon avec un titre par groupe")
    public void lesProduitsGroupesParRayon() {
        Locator grid = PlaywrightHooks.page().locator("[data-testid='product-grid']");
        assertTrue(grid.isVisible(), "La grille de produits doit être visible");
        Locator rayonTitles = grid.locator("h2");
        assertTrue(rayonTitles.count() > 0, "Il doit y avoir au moins un titre de rayon");
        for (int i = 0; i < rayonTitles.count(); i++) {
            assertFalse(rayonTitles.nth(i).textContent().isBlank(), "Le titre de rayon ne doit pas être vide");
        }
    }

    @Alors("je vois la barre de navigation par rayon")
    public void jeVoisLaBarreDeNavigationParRayon() {
        Locator nav = PlaywrightHooks.page().locator("[data-testid='rayon-nav']");
        assertTrue(nav.isVisible(), "La barre de navigation par rayon doit être visible");
    }

    @Et("chaque rayon a un bouton dans la barre de navigation")
    public void chaqueRayonAUnBoutonDansLaBarreDeNavigation() {
        Locator navButtons = PlaywrightHooks.page().locator("[data-testid='rayon-nav'] button");
        Locator rayonTitles = PlaywrightHooks.page().locator("[data-testid='product-grid'] h2");
        assertTrue(navButtons.count() > 0, "Il doit y avoir des boutons dans la navigation");
        assertEquals(rayonTitles.count(), navButtons.count(),
                "Le nombre de boutons doit correspondre au nombre de rayons");
    }

    @Alors("je vois des skeleton screens à la place des cartes")
    public void jeVoisDesSkeletonScreens() {
        // Skeleton screens are visible because API response is delayed by route interception
        Locator skeletons = PlaywrightHooks.page().locator("[data-testid='skeleton-card']");
        skeletons.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        assertTrue(skeletons.count() > 0, "Les skeleton screens doivent être visibles pendant le chargement");

        // Verify transition: products eventually load after skeletons disappear
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }
}
