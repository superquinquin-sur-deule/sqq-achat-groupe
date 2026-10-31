package fr.sqq.achatgroupe.domain.model.catalog;

import fr.sqq.achatgroupe.domain.exception.InsufficientStockException;
import fr.sqq.achatgroupe.domain.exception.VariantNotFoundException;
import fr.sqq.achatgroupe.domain.model.shared.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariantTest {

    private static ProductVariant variant(Long id, String label, String reference, String prixHt, int stock) {
        return new ProductVariant(id, 1L, label, reference, Money.eur(new BigDecimal(prixHt)), stock, null, 0);
    }

    private static Product productWith(List<ProductVariant> variants) {
        return new Product(1L, 1L, "Sapin Nordmann", "Coupe frais", new BigDecimal("20.00"), "Pepiniere",
                true, "Decoration", "Pepiniere", null, false, variants);
    }

    @Test
    void shouldRejectBlankReference() {
        var ex = assertThrows(IllegalArgumentException.class, () -> variant(1L, "150cm", "  ", "29.90", 10));
        assertTrue(ex.getMessage().contains("reference"), ex.getMessage());
    }

    @Test
    void shouldRejectNegativeStock() {
        var ex = assertThrows(IllegalArgumentException.class, () -> variant(1L, "150cm", "SAP-150", "29.90", -1));
        assertTrue(ex.getMessage().contains("stock"), ex.getMessage());
    }

    @Test
    void shouldRejectNonPositiveColisage() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new ProductVariant(
                1L, 1L, "150cm", "SAP-150", Money.eur(new BigDecimal("29.90")), 10, 0, 0));
        assertTrue(ex.getMessage().contains("colisage"), ex.getMessage());
    }

    @Test
    void shouldDecrementStock() {
        ProductVariant variant = variant(1L, "150cm", "SAP-150", "29.90", 10);
        variant.decrementStock(3, "Sapin Nordmann — 150cm");
        assertEquals(7, variant.stock());
    }

    @Test
    void shouldRejectDecrementBeyondStock() {
        ProductVariant variant = variant(1L, "150cm", "SAP-150", "29.90", 2);
        var ex = assertThrows(InsufficientStockException.class,
                () -> variant.decrementStock(3, "Sapin Nordmann — 150cm"));
        assertTrue(ex.getMessage().contains("Sapin Nordmann — 150cm"), ex.getMessage());
    }

    @Test
    void shouldIncrementStock() {
        ProductVariant variant = variant(1L, "150cm", "SAP-150", "29.90", 2);
        variant.incrementStock(5);
        assertEquals(7, variant.stock());
    }

    @Test
    void shouldRequireAtLeastOneVariant() {
        var ex = assertThrows(IllegalArgumentException.class, () -> productWith(List.of()));
        assertTrue(ex.getMessage().contains("au moins une variante"), ex.getMessage());
    }

    @Test
    void shouldRequireLabelsWhenSeveralVariants() {
        var ex = assertThrows(IllegalArgumentException.class, () -> productWith(List.of(
                variant(1L, "", "SAP-150", "29.90", 10),
                variant(2L, "200cm", "SAP-200", "39.90", 5))));
        assertTrue(ex.getMessage().contains("libellé"), ex.getMessage());
    }

    @Test
    void shouldRejectDuplicateReferences() {
        var ex = assertThrows(IllegalArgumentException.class, () -> productWith(List.of(
                variant(1L, "150cm", "SAP-150", "29.90", 10),
                variant(2L, "200cm", "SAP-150", "39.90", 5))));
        assertTrue(ex.getMessage().contains("références"), ex.getMessage());
    }

    @Test
    void shouldComputePrixTtcPerVariant() {
        ProductVariant small = variant(1L, "150cm", "SAP-150", "29.90", 10);
        ProductVariant large = variant(2L, "250cm", "SAP-250", "54.90", 3);
        Product product = productWith(List.of(small, large));

        assertEquals(0, product.prixTtc(small).amount().compareTo(new BigDecimal("35.88")));
        assertEquals(0, product.prixTtc(large).amount().compareTo(new BigDecimal("65.88")));
    }

    @Test
    void shouldBuildDisplayNameFromVariantLabel() {
        ProductVariant labelled = variant(1L, "200cm", "SAP-200", "39.90", 5);
        assertEquals("Sapin Nordmann — 200cm", productWith(List.of(labelled)).displayName(labelled));

        ProductVariant plain = variant(1L, "", "SAP-200", "39.90", 5);
        assertEquals("Sapin Nordmann", productWith(List.of(plain)).displayName(plain));
    }

    @Test
    void shouldBeAvailableWhenAnyVariantHasStock() {
        Product product = productWith(List.of(
                variant(1L, "150cm", "SAP-150", "29.90", 0),
                variant(2L, "200cm", "SAP-200", "39.90", 4)));

        assertTrue(product.isAvailable());
        assertEquals(4, product.totalStock());
    }

    @Test
    void shouldRejectUnknownVariantId() {
        Product product = productWith(List.of(variant(1L, "", "SAP-150", "29.90", 10)));
        assertThrows(VariantNotFoundException.class, () -> product.variant(999L));
    }
}
