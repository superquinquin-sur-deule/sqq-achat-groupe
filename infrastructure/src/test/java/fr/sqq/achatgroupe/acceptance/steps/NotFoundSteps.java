package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import jakarta.enterprise.context.ApplicationScoped;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class NotFoundSteps {

    @Quand("j'accède à une URL inexistante")
    public void jAccedeAUneUrlInexistante() {
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/cette-page-nexiste-pas");
        PlaywrightHooks.page().waitForSelector("[data-testid='not-found']",
                new com.microsoft.playwright.Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(15000));
    }

    @Alors("je vois la page introuvable")
    public void jeVoisLaPageIntrouvable() {
        Locator title = PlaywrightHooks.page().locator("[data-testid='not-found-title']");
        assertTrue(title.isVisible(), "Le titre 'Page introuvable' doit être visible");
        assertTrue(title.textContent().contains("Page introuvable"));

        Locator message = PlaywrightHooks.page().locator("[data-testid='not-found-message']");
        assertTrue(message.isVisible(), "Le message d'explication doit être visible");
    }

    @Et("la page affiche un bouton de retour à l'accueil")
    public void laPageAfficheUnBoutonDeRetour() {
        Locator button = PlaywrightHooks.page().locator("[data-testid='not-found'] button");
        assertTrue(button.isVisible(), "Le bouton de retour doit être visible");
        assertTrue(button.textContent().contains("Retour"));
    }

    @Et("je clique sur le bouton de retour à l'accueil")
    public void jeCliqueSurLeBoutonDeRetour() {
        PlaywrightHooks.page().locator("[data-testid='not-found'] button").click();
    }

    @Alors("je suis redirigé vers la liste des ventes")
    public void jeSuisRedirigeVersLaListeDesVentes() {
        PlaywrightHooks.page().waitForURL("**/",
                new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(15000));
        String url = PlaywrightHooks.page().url();
        assertTrue(url.endsWith("/"), "L'URL doit être la racine, got: " + url);
    }
}
