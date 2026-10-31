package fr.sqq.achatgroupe.application.service;

import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Index des variantes d'une vente, par identifiant de variante. Les écrans de restitution
 * (bon fournisseur, préparation, réception, manquants) raisonnent en variantes mais doivent
 * afficher les informations portées par le produit : cet index évite de recharger le produit
 * pour chaque ligne de commande.
 */
public record VariantIndex(Map<Long, Entry> byVariantId) {

    public record Entry(Product product, ProductVariant variant) {

        public String displayName() {
            return product.displayName(variant);
        }
    }

    public static VariantIndex of(List<Product> products) {
        Map<Long, Entry> index = new LinkedHashMap<>();
        for (Product product : products) {
            for (ProductVariant variant : product.variants()) {
                index.put(variant.id(), new Entry(product, variant));
            }
        }
        return new VariantIndex(index);
    }

    public Entry get(Long variantId) {
        return byVariantId.get(variantId);
    }

    public boolean contains(Long variantId) {
        return byVariantId.containsKey(variantId);
    }

    /** Libellé de repli, aligné sur les écrans existants lorsqu'une variante a disparu. */
    public String displayNameOr(Long variantId, String fallback) {
        Entry entry = get(variantId);
        return entry != null ? entry.displayName() : fallback;
    }
}
