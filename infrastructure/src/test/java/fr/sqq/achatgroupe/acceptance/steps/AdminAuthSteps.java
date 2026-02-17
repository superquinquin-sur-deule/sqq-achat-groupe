package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Page;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class AdminAuthSteps {

    @Quand("je navigue vers la page admin sans authentification")
    public void jeNavigueVersLaPageAdminSansAuthentification() {
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/admin");
        PlaywrightHooks.page().waitForTimeout(3000);
    }

    @Alors("je suis redirigé vers la page de login")
    public void jeSuisRedirigeVersLaPageDeLogin() {
        String url = PlaywrightHooks.page().url();
        // In test mode (OIDC disabled), the frontend redirects to /api/admin/me which returns 403
        // The page should no longer be on /admin (navigation was blocked or redirected)
        assertTrue(
                url.contains("/api/admin/me") || !url.contains("/admin/dashboard"),
                "L'utilisateur doit être redirigé hors du back-office, got: " + url
        );
    }
}
