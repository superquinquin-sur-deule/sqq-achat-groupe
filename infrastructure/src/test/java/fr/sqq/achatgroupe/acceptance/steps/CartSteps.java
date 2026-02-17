package fr.sqq.achatgroupe.acceptance.steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import fr.sqq.achatgroupe.acceptance.support.TestContext;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Et;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonnéque;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CartSteps {

    @Inject
    TestContext testContext;

    private boolean toastSeen;

    private void navigateToHome() {
        PlaywrightHooks.page().navigate(PlaywrightHooks.testUrl() + "/ventes/" + testContext.venteId());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    private void clickAddOnFirstAvailableProduct() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='add-button']").click();
    }

    private void waitForToastAndDismiss() {
        Locator toast = PlaywrightHooks.page().locator("[role='alert']");
        toast.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        toastSeen = true;
        // Wait for toast auto-dismiss
        PlaywrightHooks.page().waitForTimeout(500);
    }

    // --- Navigation steps ---

    @Quand("je navigue vers la page d'accueil")
    public void jeNavigueVersLaPageDAccueil() {
        navigateToHome();
    }

    @Quand("je navigue vers la page panier")
    public void jeNavigueVersLaPagePanier() {
        // Navigate to home first (SPA entry point) then click on cart link
        navigateToHome();
        PlaywrightHooks.page().locator("a[aria-label='Panier']").click();
        PlaywrightHooks.page().waitForURL("**/cart");
        PlaywrightHooks.page().waitForSelector("[data-testid='cart-view']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    @Quand("je consulte mon panier")
    public void jeConsulteMonPanier() {
        PlaywrightHooks.page().locator("a[aria-label='Panier']").click();
        PlaywrightHooks.page().waitForURL("**/cart");
        PlaywrightHooks.page().waitForTimeout(500);
    }

    // --- Precondition steps ---

    @Étantdonnéque("j'ai ajouté un produit au panier depuis le catalogue")
    public void jAiAjouteUnProduitAuPanier() {
        navigateToHome();
        clickAddOnFirstAvailableProduct();
        waitForToastAndDismiss();
    }

    @Étantdonnéque("j'ai ajouté plusieurs produits au panier depuis le catalogue")
    public void jAiAjoutePlusieursProduits() {
        navigateToHome();

        Locator availableCards = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])");
        int cardsToAdd = Math.min(availableCards.count(), 2);

        for (int i = 0; i < cardsToAdd; i++) {
            availableCards.nth(i).locator("[data-testid='add-button']").click();
            waitForToastAndDismiss();
        }
    }

    // --- Action steps ---

    @Quand("je recharge la page")
    public void jeRechargeLaPage() {
        PlaywrightHooks.page().reload();
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Quand("je clique sur \"Ajouter\" pour le premier produit disponible")
    public void jeCliqueSurAjouterPourLePremierProduit() {
        clickAddOnFirstAvailableProduct();
        // Immediately check for toast since it has a 3s timeout
        waitForToastAndDismiss();
    }

    @Quand("j'augmente la quantité du produit")
    public void jAugmenteLaQuantiteDuProduit() {
        Locator increaseButton = PlaywrightHooks.page().locator("[data-testid='cart-item']").first()
                .locator("[data-testid='increase-quantity']");
        increaseButton.click();
        PlaywrightHooks.page().waitForTimeout(300);
    }

    @Quand("je clique sur le bouton supprimer du produit")
    public void jeCliqueSurLeBoutonSupprimer() {
        Locator removeButton = PlaywrightHooks.page().locator("[data-testid='cart-item']").first()
                .locator("[data-testid='remove-item']");
        removeButton.click();
        PlaywrightHooks.page().waitForTimeout(300);
    }

    @Quand("j'ajoute des produits au panier")
    public void jAjouteDesProduitsAuPanier() {
        Locator availableCards = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])");
        int cardsToAdd = Math.min(availableCards.count(), 2);

        for (int i = 0; i < cardsToAdd; i++) {
            availableCards.nth(i).locator("[data-testid='add-button']").click();
            waitForToastAndDismiss();
        }
    }

    @Quand("j'augmente la quantité du produit depuis le catalogue")
    public void jAugmenteLaQuantiteDuProduitDepuisLeCatalogue() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='increase-quantity']").click();
        PlaywrightHooks.page().waitForTimeout(300);
    }

    @Quand("je diminue la quantité du produit depuis le catalogue")
    public void jeDiminueLaQuantiteDuProduitDepuisLeCatalogue() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='decrease-quantity']").click();
        PlaywrightHooks.page().waitForTimeout(300);
    }

    @Quand("je clique sur \"Continuer mes achats\"")
    public void jeCliqueSurContinuerMesAchats() {
        PlaywrightHooks.page().locator("[data-testid='continue-shopping']").click();
        PlaywrightHooks.page().waitForURL("**/ventes/" + testContext.venteId());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    @Quand("je clique sur \"Découvrir nos produits\"")
    public void jeCliqueSurDecouvrirNosProduits() {
        PlaywrightHooks.page().locator("[data-testid='discover-products']").click();
        PlaywrightHooks.page().waitForURL("**/ventes/" + testContext.venteId());
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }

    // --- Assertion steps ---

    @Alors("le produit est ajouté à mon panier")
    public void leProduitEstAjouteAMonPanier() {
        Locator cartBadge = PlaywrightHooks.page().locator("[data-testid='cart-count']");
        cartBadge.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String count = cartBadge.textContent().trim();
        assertTrue(Integer.parseInt(count) > 0, "Le compteur du panier doit être supérieur à 0");
    }

    @Alors("le produit est toujours dans mon panier")
    public void leProduitEstToujoursDansMonPanier() {
        Locator cartBadge = PlaywrightHooks.page().locator("[data-testid='cart-count']");
        cartBadge.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String count = cartBadge.textContent().trim();
        assertTrue(Integer.parseInt(count) > 0, "Le panier doit contenir des articles après rechargement de la page");
    }

    @Et("un toast \"Produit ajouté\" apparaît")
    public void unToastProduitAjouteApparait() {
        // Toast was already verified when clicking "Ajouter" (captured in waitForToastAndDismiss)
        assertTrue(toastSeen, "Le toast 'Produit ajouté' doit être apparu après l'ajout");
    }

    @Alors("le sous-total de la ligne et le total général se mettent à jour")
    public void leSousTotalEtTotalSeMettentAJour() {
        Locator cartItem = PlaywrightHooks.page().locator("[data-testid='cart-item']").first();

        String quantity = cartItem.locator("[data-testid='item-quantity']").textContent().trim();
        assertEquals("2", quantity, "La quantité doit être 2 après incrémentation");

        Locator subtotal = cartItem.locator("[data-testid='item-subtotal']");
        assertTrue(subtotal.isVisible(), "Le sous-total doit être visible");

        Locator total = PlaywrightHooks.page().locator("[data-testid='cart-total']");
        assertTrue(total.isVisible(), "Le total général doit être visible");
    }

    @Alors("le produit est retiré du panier")
    public void leProduitEstRetireDuPanier() {
        PlaywrightHooks.page().waitForTimeout(300);
        Locator emptyMessage = PlaywrightHooks.page().locator("[data-testid='empty-cart']");
        Locator cartItems = PlaywrightHooks.page().locator("[data-testid='cart-item']");

        boolean cartIsEmpty = emptyMessage.isVisible();
        boolean itemRemoved = cartItems.count() == 0;
        assertTrue(cartIsEmpty || itemRemoved, "Le produit doit être retiré du panier");
    }

    @Et("le total se met à jour")
    public void leTotalSeMetAJour() {
        Locator emptyCart = PlaywrightHooks.page().locator("[data-testid='empty-cart']");
        if (emptyCart.isVisible()) {
            return;
        }
        Locator total = PlaywrightHooks.page().locator("[data-testid='cart-total']");
        assertTrue(total.isVisible(), "Le total doit être visible et mis à jour");
    }

    @Alors("je vois chaque produit avec son nom, prix unitaire, quantité et sous-total")
    public void jeVoisChaqueProduitAvecDetails() {
        Locator cartItems = PlaywrightHooks.page().locator("[data-testid='cart-item']");
        assertTrue(cartItems.count() > 0, "Il doit y avoir des produits dans le panier");

        for (int i = 0; i < cartItems.count(); i++) {
            Locator item = cartItems.nth(i);
            assertTrue(item.locator("[data-testid='item-name']").isVisible(),
                    "Le nom du produit doit être visible (item " + i + ")");
            assertTrue(item.locator("[data-testid='item-price']").isVisible(),
                    "Le prix unitaire doit être visible (item " + i + ")");
            assertTrue(item.locator("[data-testid='item-quantity']").isVisible(),
                    "La quantité doit être visible (item " + i + ")");
            assertTrue(item.locator("[data-testid='item-subtotal']").isVisible(),
                    "Le sous-total doit être visible (item " + i + ")");
        }
    }

    @Et("je vois le total général en bas")
    public void jeVoisLeTotalGeneralEnBas() {
        Locator total = PlaywrightHooks.page().locator("[data-testid='cart-total']");
        assertTrue(total.isVisible(), "Le total général doit être visible");
        String totalText = total.textContent();
        assertNotNull(totalText);
        assertFalse(totalText.isBlank(), "Le total ne doit pas être vide");
    }

    @Alors("le compteur du bouton panier dans le header affiche le nombre correct d'articles")
    public void leCompteurDuBoutonPanierAfficheLeNombreCorrect() {
        Locator cartBadge = PlaywrightHooks.page().locator("[data-testid='cart-count']");
        cartBadge.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String count = cartBadge.textContent().trim();
        int itemCount = Integer.parseInt(count);
        assertTrue(itemCount >= 2, "Le compteur doit afficher au moins 2 articles, got: " + itemCount);
    }

    @Alors("le bouton \"Ajouter\" du premier produit est remplacé par un sélecteur de quantité")
    public void leBoutonAjouterEstRemplaceParUnSelecteurDeQuantite() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        assertFalse(firstCard.locator("[data-testid='add-button']").isVisible(),
                "Le bouton 'Ajouter' ne doit plus être visible");
        assertTrue(firstCard.locator("[data-testid='decrease-quantity']").isVisible(),
                "Le bouton '-' doit être visible");
        assertTrue(firstCard.locator("[data-testid='item-quantity']").isVisible(),
                "L'input de quantité doit être visible");
        assertTrue(firstCard.locator("[data-testid='increase-quantity']").isVisible(),
                "Le bouton '+' doit être visible");
    }

    @Alors("le sélecteur affiche la quantité {int}")
    public void leSelecteurAfficheLaQuantite(int expectedQuantity) {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        String value = firstCard.locator("[data-testid='item-quantity']").inputValue();
        assertEquals(String.valueOf(expectedQuantity), value,
                "La quantité affichée doit être " + expectedQuantity);
    }

    @Alors("le compteur du panier dans le header affiche {int}")
    public void leCompteurDuPanierDansLeHeaderAffiche(int expectedCount) {
        Locator cartBadge = PlaywrightHooks.page().locator("[data-testid='cart-count']");
        cartBadge.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        String count = cartBadge.textContent().trim();
        assertEquals(String.valueOf(expectedCount), count,
                "Le compteur du panier doit afficher " + expectedCount);
    }

    @Alors("le bouton \"Ajouter\" réapparaît sur le premier produit")
    public void leBoutonAjouterReapparaitSurLePremierProduit() {
        Locator firstCard = PlaywrightHooks.page().locator("[data-testid='product-card']:not([data-exhausted='true'])").first();
        firstCard.locator("[data-testid='add-button']").waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        assertTrue(firstCard.locator("[data-testid='add-button']").isVisible(),
                "Le bouton 'Ajouter' doit réapparaître");
        assertFalse(firstCard.locator("[data-testid='decrease-quantity']").isVisible(),
                "Le sélecteur de quantité ne doit plus être visible");
    }

    @Alors("le compteur du panier dans le header n'est plus visible")
    public void leCompteurDuPanierNEstPlusVisible() {
        Locator cartBadge = PlaywrightHooks.page().locator("[data-testid='cart-count']");
        cartBadge.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(5000));
        assertFalse(cartBadge.isVisible(), "Le compteur du panier ne doit plus être visible");
    }

    @Alors("le panier affiche le message \"Votre panier est vide\"")
    public void jeVoisLeMessagePanierVide() {
        Locator emptyMessage = PlaywrightHooks.page().locator("[data-testid='empty-cart']");
        emptyMessage.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
        String text = emptyMessage.textContent();
        assertNotNull(text);
        assertTrue(text.contains("Votre panier est vide"), "Le message 'Votre panier est vide' doit être affiché");
    }

    @Et("je vois un bouton \"Découvrir nos produits\"")
    public void jeVoisUnBoutonDecouvrirNosProduits() {
        Locator button = PlaywrightHooks.page().locator("[data-testid='discover-products']");
        assertTrue(button.isVisible(), "Le bouton 'Découvrir nos produits' doit être visible");
        String text = button.textContent();
        assertNotNull(text);
        assertTrue(text.contains("Découvrir nos produits"), "Le bouton doit contenir 'Découvrir nos produits'");
    }

    @Alors("je suis redirigé vers le catalogue")
    public void jeSuisRedirigeVersLeCatalogue() {
        PlaywrightHooks.page().waitForSelector("[data-testid='product-card']", new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
    }
}
