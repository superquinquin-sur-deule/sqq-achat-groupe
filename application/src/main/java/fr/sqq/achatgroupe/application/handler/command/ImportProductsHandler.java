package fr.sqq.achatgroupe.application.handler.command;

import fr.sqq.achatgroupe.application.command.ImportProductsCommand;
import fr.sqq.achatgroupe.application.command.ImportProductsCommand.CsvProductRow;
import fr.sqq.achatgroupe.application.command.ImportResult;
import fr.sqq.achatgroupe.application.port.out.ProductRepository;
import fr.sqq.achatgroupe.domain.model.catalog.Product;
import fr.sqq.achatgroupe.domain.model.catalog.ProductVariant;
import fr.sqq.mediator.CommandHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class ImportProductsHandler implements CommandHandler<ImportProductsCommand, ImportResult> {

    private final ProductRepository productRepository;

    public ImportProductsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ImportResult handle(ImportProductsCommand command) {
        Log.infof("Importing products for %s", command.venteId());
        List<ImportResult.ImportError> errors = new ArrayList<>();
        int imported = 0;

        for (RowGroup group : groupRows(command.rows())) {
            try {
                productRepository.saveNew(toProduct(command.venteId(), group));
                imported++;
            } catch (IllegalArgumentException e) {
                errors.add(new ImportResult.ImportError(group.firstLineNumber(), e.getMessage()));
            }
        }

        return new ImportResult(imported, errors);
    }

    /**
     * Regroupe les lignes consécutives qui portent une variante et partagent le même nom :
     * elles forment un seul produit à plusieurs variantes. Une ligne sans variante reste
     * un produit autonome, à variante unique au libellé vide.
     */
    private List<RowGroup> groupRows(List<CsvProductRow> rows) {
        List<RowGroup> groups = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            CsvProductRow row = rows.get(i);
            int lineNumber = i + 2;
            RowGroup current = groups.isEmpty() ? null : groups.get(groups.size() - 1);

            boolean continuesGroup = current != null
                    && hasVariante(row)
                    && hasVariante(current.rows().get(0))
                    && Objects.equals(current.rows().get(0).name(), row.name());

            if (continuesGroup) {
                current.rows().add(row);
            } else {
                groups.add(new RowGroup(lineNumber, new ArrayList<>(List.of(row))));
            }
        }
        return groups;
    }

    private Product toProduct(Long venteId, RowGroup group) {
        CsvProductRow head = group.rows().get(0);
        List<ProductVariant> variants = new ArrayList<>();

        for (int i = 0; i < group.rows().size(); i++) {
            CsvProductRow row = group.rows().get(i);
            if (i > 0) {
                checkConsistentProductFields(head, row);
            }
            variants.add(new ProductVariant(
                    null,
                    null,
                    hasVariante(row) ? row.variante() : "",
                    row.reference(),
                    row.prixHt(),
                    row.stock(),
                    row.colisage(),
                    i
            ));
        }

        return new Product(
                null,
                venteId,
                head.name(),
                head.description(),
                head.tauxTva(),
                head.supplier(),
                true,
                head.category(),
                head.brand(),
                head.importantNote(),
                false,
                variants
        );
    }

    /** Les colonnes de niveau produit doivent être identiques sur toutes les lignes d'un groupe. */
    private void checkConsistentProductFields(CsvProductRow head, CsvProductRow row) {
        if (!Objects.equals(head.description(), row.description())
                || head.tauxTva().compareTo(row.tauxTva()) != 0
                || !Objects.equals(head.supplier(), row.supplier())
                || !Objects.equals(head.category(), row.category())
                || !Objects.equals(head.brand(), row.brand())
                || !Objects.equals(head.importantNote(), row.importantNote())) {
            throw new IllegalArgumentException(
                    "Les variantes de \"" + head.name() + "\" doivent partager la même description, TVA, "
                            + "fournisseur, catégorie, marque et mention importante");
        }
    }

    private boolean hasVariante(CsvProductRow row) {
        return row.variante() != null && !row.variante().isBlank();
    }

    private record RowGroup(int firstLineNumber, List<CsvProductRow> rows) {
    }
}
