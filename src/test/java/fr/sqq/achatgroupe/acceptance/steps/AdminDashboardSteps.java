package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class AdminDashboardSteps {

    @Inject
    TestContext testContext;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page admin dashboard")
    public void jeNavigueVersLaPageAdminDashboard() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/dashboard");
        page().waitForSelector("[data-testid='dashboard-stats']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois la zone des statistiques")
    public void jeVoisLaZoneDesStatistiques() {
        Locator stats = page().locator("[data-testid='dashboard-stats']");
        assertTrue(stats.isVisible(), "La zone des statistiques doit être visible");
    }

    @Et("je vois la carte {string}")
    public void jeVoisLaCarte(String cardLabel) {
        // Use dashboard-stats scope to avoid matching elements outside stat cards
        Locator statsZone = page().locator("[data-testid='dashboard-stats']");
        Locator card = statsZone.getByText(cardLabel, new Locator.GetByTextOptions().setExact(true));
        card.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertTrue(card.first().isVisible(), "La carte '" + cardLabel + "' doit être visible");
    }

    @Et("je vois le tableau de répartition par créneau")
    public void jeVoisLeTableauDeRepartition() {
        Locator table = page().locator("[data-testid='slot-distribution-table']");
        assertTrue(table.isVisible(), "Le tableau de répartition par créneau doit être visible");
    }
}
