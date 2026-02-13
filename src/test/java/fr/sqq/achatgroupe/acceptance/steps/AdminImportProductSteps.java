package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

@ApplicationScoped
public class AdminImportProductSteps {

    private Page page() {
        return PlaywrightHooks.page();
    }

    @Quand("je navigue vers la page admin produits")
    public void jeNavigueVersLaPageAdminProduits() {
        // Intercepter /api/admin/me pour simuler un utilisateur authentifié (OIDC désactivé en test)
        page().route("**/api/admin/me", route -> route.fulfill(new Route.FulfillOptions()
                .setStatus(200)
                .setContentType("application/json")
                .setBody("{\"data\":{\"name\":\"Test Admin\",\"email\":\"admin@test.fr\"}}")));

        page().navigate(PlaywrightHooks.testUrl() + "/admin/products");
        page().waitForSelector("[data-testid='add-product-btn']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Et("je clique sur \"Importer des produits\"")
    public void jeCliqueSurImporterDesProduits() {
        page().locator("[data-testid='import-btn']").click();
        page().waitForSelector("[data-testid='import-form']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
    }

    @Alors("je vois le formulaire d'import CSV")
    public void jeVoisLeFormulaireDImportCsv() {
        Locator form = page().locator("[data-testid='import-form']");
        assertTrue(form.isVisible(), "Le formulaire d'import doit être visible");

        Locator fileInput = page().locator("[data-testid='import-file-input']");
        assertNotNull(fileInput, "L'input file doit exister");

        Locator submitBtn = page().locator("[data-testid='import-submit-btn']");
        assertTrue(submitBtn.isVisible(), "Le bouton Importer doit être visible");
        assertTrue(submitBtn.isDisabled(), "Le bouton Importer doit être désactivé sans fichier");
    }

    @Et("j'uploade le fichier CSV {string}")
    public void jUploadeUnFichierCsv(String csvPath) {
        Path filePath = loadTestResourcePath(csvPath);
        page().locator("[data-testid='import-file-input']").setInputFiles(filePath);
    }

    @Et("j'uploade le fichier {string}")
    public void jUploadeUnFichier(String filePath) {
        Path path = loadTestResourcePath(filePath);
        page().locator("[data-testid='import-file-input']").setInputFiles(path);
    }

    @Et("je clique sur le bouton \"Importer\"")
    public void jeCliqueSurLeBoutonImporter() {
        page().locator("[data-testid='import-submit-btn']").click();
        // Attendre que le toast apparaisse (succès, warning ou erreur)
        page().waitForSelector("[role='alert']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Alors("je vois un toast de succès d'import")
    public void jeVoisUnToastDeSuccesDImport() {
        Locator toast = page().locator("[role='alert']").first();
        toast.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String text = toast.textContent();
        assertNotNull(text);
        assertTrue(text.contains("importé"), "Le toast doit contenir 'importé', got: " + text);
    }

    @Et("la liste des produits est rafraîchie")
    public void laListeDesProduitsEstRafraichie() {
        page().waitForSelector("[data-testid='product-table']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        Locator rows = page().locator("[data-testid='product-row']");
        assertTrue(rows.count() > 0, "La table des produits doit contenir des lignes après l'import");
    }

    @Alors("je vois un toast d'avertissement d'import")
    public void jeVoisUnToastDAvertissementDImport() {
        Locator toast = page().locator("[role='alert']").first();
        toast.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String text = toast.textContent();
        assertNotNull(text);
        assertTrue(text.contains("importé") && text.contains("erreur"),
                "Le toast doit contenir 'importé' et 'erreur', got: " + text);
    }

    @Et("je vois le rapport d'erreurs avec le tableau des détails")
    public void jeVoisLeRapportDErreursAvecLeTableauDesDetails() {
        Locator report = page().locator("[data-testid='import-report']");
        report.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertTrue(report.isVisible(), "Le rapport d'import doit être visible");

        Locator errorTable = page().locator("[data-testid='import-error-table']");
        assertTrue(errorTable.isVisible(), "Le tableau d'erreurs doit être visible");

        Locator errorRows = errorTable.locator("tbody tr");
        assertTrue(errorRows.count() > 0, "Le tableau d'erreurs doit contenir des lignes");
    }

    @Alors("je vois un toast d'erreur d'import")
    public void jeVoisUnToastDErreurDImport() {
        Locator toast = page().locator("[role='alert']").first();
        toast.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertTrue(toast.isVisible(), "Un toast d'erreur doit être affiché");
    }

    private Path loadTestResourcePath(String resourcePath) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid resource URI: " + resourcePath, e);
        }
    }
}
