package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class AdminTimeSlotSteps {

    @Inject
    TestContext testContext;

    private int initialRowCount;
    private boolean hasReservationsScenario = false;

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page admin créneaux")
    public void jeNavigueVersLaPageAdminCreneaux() {
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        if (hasReservationsScenario) {
            // Intercept the timeslots API to modify the response: add a timeslot with reservations
            page().route("**/api/admin/timeslots**", route -> {
                var response = route.fetch();
                String originalBody = new String(response.body());
                // Inject a timeslot with reservations into the response data array
                LocalDate futureDate = LocalDate.now().plusDays(30);
                String injectedSlot = """
                        {"id":99999,"date":"%s","startTime":"16:00","endTime":"17:00","capacity":10,"reserved":3,"remainingPlaces":7}""".formatted(futureDate);
                String modifiedBody = originalBody.replace("]}", "," + injectedSlot + "]}");
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(response.status())
                        .setHeaders(response.headers())
                        .setBody(modifiedBody));
            });
        }

        page().navigate(PlaywrightHooks.testUrl() + "/admin/timeslots");
        page().waitForSelector("[data-testid='timeslot-table'], [data-testid='empty-state']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Alors("je vois la table des créneaux avec les colonnes Date, Horaire, Capacité, Réservations et Actions")
    public void jeVoisLaTableDesCreneaux() {
        Locator table = page().locator("[data-testid='timeslot-table']");
        assertTrue(table.isVisible(), "La table des créneaux doit être visible");

        String headerText = table.locator("thead").textContent();
        assertTrue(headerText.contains("Date"), "La colonne Date doit exister");
        assertTrue(headerText.contains("Horaire"), "La colonne Horaire doit exister");
        assertTrue(headerText.contains("Capacité"), "La colonne Capacité doit exister");
        assertTrue(headerText.contains("Réservations"), "La colonne Réservations doit exister");
        assertTrue(headerText.contains("Actions"), "La colonne Actions doit exister");
    }

    @Et("chaque ligne affiche les informations du créneau")
    public void chaqueLigneAfficheLesInformations() {
        Locator rows = page().locator("[data-testid='timeslot-row']");
        assertTrue(rows.count() > 0, "Il doit y avoir au moins une ligne de créneau");

        for (int i = 0; i < rows.count(); i++) {
            Locator row = rows.nth(i);
            Locator cells = row.locator("td");
            assertTrue(cells.count() >= 5, "Chaque ligne doit avoir au moins 5 cellules");
            assertFalse(cells.nth(0).textContent().isBlank(), "La date ne doit pas être vide");
            assertFalse(cells.nth(1).textContent().isBlank(), "L'horaire ne doit pas être vide");
        }
    }

    @Et("je clique sur \"Ajouter un créneau\"")
    public void jeCliqueSurAjouterUnCreneau() {
        initialRowCount = page().locator("[data-testid='timeslot-row']").count();
        page().locator("[data-testid='add-timeslot-btn']").click();
        page().waitForSelector("[data-testid='timeslot-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Et("je remplis le formulaire créneau avec une date future, de {word} à {word} et une capacité de {int}")
    public void jeRemplisLeFormulaireCreneau(String startTime, String endTime, int capacity) {
        LocalDate futureDate = LocalDate.now().plusDays(30);

        page().locator("[data-testid='timeslot-date-input']").fill(futureDate.toString());
        page().locator("[data-testid='timeslot-start-input']").fill(startTime);
        page().locator("[data-testid='timeslot-end-input']").fill(endTime);
        page().locator("[data-testid='timeslot-capacity-input']").fill(String.valueOf(capacity));
    }

    @Et("je clique sur le bouton \"Enregistrer\" du formulaire créneau")
    public void jeCliqueSurEnregistrer() {
        page().locator("[data-testid='timeslot-submit-btn']").click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("je vois un toast de succès {string}")
    public void jeVoisUnToastDeSucces(String expectedMessage) {
        Locator toast = page().locator("[role='alert']").first();
        toast.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String text = toast.textContent();
        assertNotNull(text);
        assertTrue(text.contains(expectedMessage), "Le toast doit contenir '" + expectedMessage + "', got: " + text);
    }

    @Et("la table des créneaux contient un créneau de plus")
    public void laTableContientUnCreneauDePlus() {
        page().waitForSelector("[data-testid='timeslot-table']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        Locator rows = page().locator("[data-testid='timeslot-row']");
        assertEquals(initialRowCount + 1, rows.count(), "La table doit contenir un créneau de plus");
    }

    @Et("je clique sur \"Modifier\" du premier créneau")
    public void jeCliqueSurModifierDuPremierCreneau() {
        Locator firstRow = page().locator("[data-testid='timeslot-row']").first();
        firstRow.locator("button", new Locator.LocatorOptions().setHasText("Modifier")).click();
        page().waitForSelector("[data-testid='timeslot-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Et("je modifie la capacité du créneau à {int}")
    public void jeModifieLaCapacite(int capacity) {
        Locator capacityInput = page().locator("[data-testid='timeslot-capacity-input']");
        capacityInput.fill(String.valueOf(capacity));
    }

    @Et("je clique sur \"Supprimer\" du premier créneau")
    public void jeCliqueSurSupprimerDuPremierCreneau() {
        initialRowCount = page().locator("[data-testid='timeslot-row']").count();
        Locator firstRow = page().locator("[data-testid='timeslot-row']").first();
        firstRow.locator("button", new Locator.LocatorOptions().setHasText("Supprimer")).click();
    }

    @Et("je confirme la suppression du créneau")
    public void jeConfirmeLaSuppression() {
        Locator firstRow = page().locator("[data-testid='timeslot-row']").first();
        Locator confirmBtn = firstRow.locator("button", new Locator.LocatorOptions().setHasText("Supprimer"));
        confirmBtn.click();
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Et("la table des créneaux contient un créneau de moins")
    public void laTableContientUnCreneauDeMoins() {
        page().waitForSelector("[data-testid='timeslot-table']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        Locator rows = page().locator("[data-testid='timeslot-row']");
        assertEquals(initialRowCount - 1, rows.count(), "La table doit contenir un créneau de moins");
    }

    @Étantdonnéque("un créneau a des réservations")
    public void unCreneauADesReservations() {
        hasReservationsScenario = true;
        // We intercept the admin timeslots API response in the browser to inject a timeslot with reservations
        // This is done when navigating to the page
    }

    @Et("je clique sur \"Supprimer\" du créneau avec réservations")
    public void jeCliqueSurSupprimerDuCreneauAvecReservations() {
        // Find the row that contains the injected timeslot with 3 reservations (3 / 10)
        Locator rows = page().locator("[data-testid='timeslot-row']");
        boolean found = false;
        for (int i = 0; i < rows.count(); i++) {
            Locator row = rows.nth(i);
            String text = row.textContent();
            if (text.contains("3 / 10")) {
                row.locator("button", new Locator.LocatorOptions().setHasText("Supprimer")).click();
                found = true;
                break;
            }
        }
        assertTrue(found, "Le créneau avec réservations (3 / 10) doit être trouvé dans la table. Lignes: " + rows.count());
    }

    @Alors("je vois un avertissement de réservations sur le créneau")
    public void jeVoisUnAvertissementDeReservations() {
        Locator warning = page().getByText("La suppression est irréversible");
        warning.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertTrue(warning.isVisible(), "L'avertissement de réservations doit être visible");
    }

    @Et("je soumets le formulaire créneau sans remplir les champs")
    public void jeSoumetsLeFormulaireSansRemplir() {
        page().locator("[data-testid='timeslot-submit-btn']").click();
    }

    @Alors("je vois des erreurs de validation sur le formulaire créneau")
    public void jeVoisDesErreursDeValidation() {
        // Wait for validation error messages to appear after submit
        page().waitForTimeout(500);
        Locator errors = page().locator("[data-testid='timeslot-form'] p[role='alert']");
        if (errors.count() == 0) {
            // Fallback: check for error paragraphs with red text
            errors = page().locator("[data-testid='timeslot-form'] .text-red-600");
        }
        assertTrue(errors.count() > 0, "Il doit y avoir des erreurs de validation affichées");
    }
}
