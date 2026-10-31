package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand;
import fr.sqq.achatgroupe.application.command.CreateOrderCommand.OrderItemCommand;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery;
import fr.sqq.achatgroupe.application.query.GenerateSupplierOrderQuery.SupplierOrderLine;
import fr.sqq.mediator.Mediator;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class ProductVariantSteps {

    @Inject
    TestContext testContext;

    @Inject
    Mediator mediator;

    private List<SupplierOrderLine> supplierOrderLines;

    private Page page() {
        return PlaywrightHooks.page();
    }

    private Locator cardOf(String productName) {
        return page().locator("[data-testid='product-card']")
                .filter(new Locator.FilterOptions().setHasText(productName))
                .first();
    }

    private Locator variantOption(String productName, String label) {
        return cardOf(productName).locator("[data-testid='variant-option']")
                .filter(new Locator.FilterOptions().setHasText(label))
                .first();
    }

    @Alors("la carte du produit {string} affiche {int} variantes")
    public void laCarteDuProduitAfficheNVariantes(String productName, int expected) {
        Locator options = cardOf(productName).locator("[data-testid='variant-option']");
        options.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        assertEquals(expected, options.count(),
                "La carte de " + productName + " doit afficher " + expected + " variantes");
    }

    @Et("la variante {string} affiche le prix {string}")
    public void laVarianteAfficheLePrix(String label, String expectedPrice) {
        Locator option = page().locator("[data-testid='variant-option']")
                .filter(new Locator.FilterOptions().setHasText(label))
                .first();
        String price = option.locator("[data-testid='variant-option-price']").textContent().trim();
        assertEquals(expectedPrice, normalize(price),
                "Le prix affiché pour la variante " + label);
    }

    @Alors("aucune variante du produit {string} n'est sélectionnée")
    public void aucuneVarianteNEstSelectionnee(String productName) {
        Locator selected = cardOf(productName).locator("[data-testid='variant-option'][data-selected='true']");
        assertEquals(0, selected.count(), "Aucune variante ne doit être présélectionnée");
    }

    @Et("le bouton Ajouter du produit {string} est désactivé")
    public void leBoutonAjouterEstDesactive(String productName) {
        assertTrue(cardOf(productName).locator("[data-testid='add-button']").isDisabled(),
                "Le bouton Ajouter doit être désactivé tant qu'aucune variante n'est choisie");
    }

    @Alors("le bouton Ajouter du produit {string} est actif")
    public void leBoutonAjouterEstActif(String productName) {
        assertFalse(cardOf(productName).locator("[data-testid='add-button']").isDisabled(),
                "Le bouton Ajouter doit être actif");
    }

    @Et("je sélectionne la variante {string} du produit {string}")
    public void jeSelectionneLaVariante(String label, String productName) {
        variantOption(productName, label).click();
    }

    @Quand("je clique sur {string} pour le produit {string}")
    public void jeCliqueSurAjouterPourLeProduit(String buttonLabel, String productName) {
        cardOf(productName).locator("[data-testid='add-button']").click();
        page().waitForTimeout(300);
    }

    @Et("j'ajoute la variante {string} du produit {string} au panier")
    public void jAjouteLaVarianteAuPanier(String label, String productName) {
        variantOption(productName, label).click();
        cardOf(productName).locator("[data-testid='add-button']").click();
        page().waitForTimeout(300);
    }

    @Alors("la variante {string} du produit {string} est désactivée")
    public void laVarianteEstDesactivee(String label, String productName) {
        Locator option = variantOption(productName, label);
        assertTrue(option.isDisabled(), "La variante épuisée " + label + " doit être désactivée");
        assertEquals("true", option.getAttribute("data-exhausted"),
                "La variante épuisée doit porter data-exhausted");
    }

    @Alors("la carte du produit {string} n'affiche pas de sélecteur de variante")
    public void laCarteNAffichePasDeSelecteur(String productName) {
        assertEquals(0, cardOf(productName).locator("[data-testid='variant-selector']").count(),
                "Un produit à variante unique ne doit pas afficher de sélecteur");
    }

    @Alors("le panier contient {int} lignes")
    public void lePanierContientNLignes(int expected) {
        assertEquals(expected, page().locator("[data-testid='cart-item']").count(),
                "Le panier doit contenir " + expected + " lignes");
    }

    @Alors("le panier contient la ligne {string}")
    public void lePanierContientLaLigne(String expectedLabel) {
        openCart();
        assertTrue(cartLabels().contains(expectedLabel),
                "Le panier doit contenir la ligne « " + expectedLabel + " », trouvé : " + cartLabels());
    }

    @Et("le panier contient la ligne {string} au prix {string}")
    public void lePanierContientLaLigneAuPrix(String expectedLabel, String expectedPrice) {
        Locator line = page().locator("[data-testid='cart-item']")
                .filter(new Locator.FilterOptions().setHasText(expectedLabel))
                .first();
        assertEquals(1, line.count(), "La ligne « " + expectedLabel + " » doit exister");
        assertEquals(expectedPrice, normalize(line.locator("[data-testid='item-price']").textContent().trim()),
                "Le prix unitaire de « " + expectedLabel + " »");
    }

    private void openCart() {
        page().locator("a[aria-label='Panier']").click();
        page().waitForURL("**/cart");
        page().waitForSelector("[data-testid='cart-item']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    private List<String> cartLabels() {
        return page().locator("[data-testid='cart-item'] [data-testid='item-name']")
                .allTextContents().stream().map(String::trim).toList();
    }

    /** Les espaces insécables du format monétaire français perturbent les comparaisons. */
    private String normalize(String value) {
        return value.replace(' ', ' ').replace(' ', ' ').trim();
    }

    // ---------- Scénario API (sans navigateur) ----------

    @Quand("je commande {int} unités de la variante {string} et {int} unités de la variante {string}")
    @Transactional
    public void jeCommandeDeuxVariantes(int qty1, String label1, int qty2, String label2) {
        // variantIds = [couronne, sapin 150cm, sapin 200cm, sapin 250cm]
        Long variant1 = testContext.variantIds().get(1);
        Long variant2 = testContext.variantIds().get(2);

        mediator.send(new CreateOrderCommand(
                testContext.venteId(), "Alice", "Dupont", "alice@test.fr", "0612345678",
                testContext.timeSlotIds().get(0), null,
                List.of(new OrderItemCommand(variant1, qty1), new OrderItemCommand(variant2, qty2))));

        supplierOrderLines = mediator.send(new GenerateSupplierOrderQuery(testContext.venteId()));
    }

    @Alors("le bon fournisseur contient une ligne {string} de {int} unités avec un colisage de {int}")
    public void leBonFournisseurContientUneLigne(String reference, int quantity, int colisage) {
        SupplierOrderLine line = lineOf(reference);
        assertEquals(quantity, line.totalQuantity(), "Quantité de la ligne " + reference);
        assertEquals(colisage, line.colisage(), "Colisage de la ligne " + reference);
    }

    @Et("le bon fournisseur nomme la ligne {string} {string}")
    public void leBonFournisseurNommeLaLigne(String reference, String expectedName) {
        assertEquals(expectedName, lineOf(reference).productName(),
                "Le libellé de la ligne " + reference);
    }

    private SupplierOrderLine lineOf(String reference) {
        Optional<SupplierOrderLine> line = supplierOrderLines.stream()
                .filter(l -> reference.equals(l.reference()))
                .findFirst();
        assertTrue(line.isPresent(), "Le bon fournisseur doit contenir la référence " + reference
                + ", trouvé : " + supplierOrderLines.stream().map(SupplierOrderLine::reference).toList());
        return line.get();
    }
}
