package fr.sqq.achatgroupe.domain.model.catalog;

import fr.sqq.achatgroupe.domain.exception.VariantNotFoundException;
import fr.sqq.achatgroupe.domain.model.shared.Money;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class Product {

    public static final int MAX_IMPORTANT_NOTE_LENGTH = 500;
    public static final String VARIANT_SEPARATOR = " — ";

    private final Long id;
    private final Long venteId;
    private final String name;
    private final String description;
    private final BigDecimal tauxTva;
    private final String supplier;
    private final boolean active;
    private final String category;
    private final String brand;
    private final String importantNote;
    private final boolean hasImage;
    private final List<ProductVariant> variants;

    public Product(Long id, Long venteId, String name, String description, BigDecimal tauxTva, String supplier,
                   boolean active, String category, String brand, String importantNote, boolean hasImage,
                   List<ProductVariant> variants) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (tauxTva == null || tauxTva.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product VAT rate must not be negative");
        }
        if (supplier == null || supplier.isBlank()) {
            throw new IllegalArgumentException("Product supplier must not be blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category must not be blank");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Product brand must not be blank");
        }
        if (importantNote != null && importantNote.length() > MAX_IMPORTANT_NOTE_LENGTH) {
            throw new IllegalArgumentException("Product important note must not exceed " + MAX_IMPORTANT_NOTE_LENGTH + " characters");
        }
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("Un produit doit avoir au moins une variante");
        }
        if (variants.size() > 1 && variants.stream().anyMatch(v -> !v.isNamed())) {
            throw new IllegalArgumentException("Chaque variante doit avoir un libellé lorsque le produit en a plusieurs");
        }
        if (variants.stream().map(ProductVariant::label).distinct().count() != variants.size()) {
            throw new IllegalArgumentException("Les libellés de variantes doivent être uniques");
        }
        if (variants.stream().map(ProductVariant::reference).distinct().count() != variants.size()) {
            throw new IllegalArgumentException("Les références de variantes doivent être uniques");
        }
        this.id = id;
        this.venteId = venteId;
        this.name = name;
        this.description = description;
        this.tauxTva = tauxTva;
        this.supplier = supplier;
        this.active = active;
        this.category = category;
        this.brand = brand;
        this.importantNote = normalizeImportantNote(importantNote);
        this.hasImage = hasImage;
        this.variants = variants.stream()
                .sorted(Comparator.comparingInt(ProductVariant::position))
                .toList();
    }

    public ProductVariant variant(Long variantId) {
        return variants.stream()
                .filter(v -> v.id() != null && v.id().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new VariantNotFoundException(variantId));
    }

    public Money prixTtc(ProductVariant variant) {
        return variant.prixHt().multiply(BigDecimal.ONE.add(tauxTva.divide(BigDecimal.valueOf(100))));
    }

    public String displayName(ProductVariant variant) {
        return variant.isNamed() ? name + VARIANT_SEPARATOR + variant.label() : name;
    }

    public String displayName(Long variantId) {
        return displayName(variant(variantId));
    }

    public boolean hasMultipleVariants() {
        return variants.size() > 1;
    }

    public ProductVariant soleVariant() {
        if (hasMultipleVariants()) {
            throw new IllegalStateException("Le produit \"" + name + "\" a plusieurs variantes");
        }
        return variants.get(0);
    }

    public int totalStock() {
        return variants.stream().mapToInt(ProductVariant::stock).sum();
    }

    public boolean isAvailable() {
        return variants.stream().anyMatch(ProductVariant::isAvailable);
    }

    public Long id() {
        return id;
    }

    public Long venteId() {
        return venteId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public BigDecimal tauxTva() {
        return tauxTva;
    }

    public String supplier() {
        return supplier;
    }

    public boolean active() {
        return active;
    }

    public String category() {
        return category;
    }

    public String brand() {
        return brand;
    }

    public String importantNote() {
        return importantNote;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public List<ProductVariant> variants() {
        return variants;
    }

    private static String normalizeImportantNote(String importantNote) {
        if (importantNote == null || importantNote.isBlank()) {
            return null;
        }
        return importantNote.strip();
    }
}
