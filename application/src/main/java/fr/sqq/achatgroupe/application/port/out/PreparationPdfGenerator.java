package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.GeneratePreparationListQuery.PreparationOrder;

import java.io.IOException;
import java.util.List;

public interface PreparationPdfGenerator {
    byte[] generate(List<PreparationOrder> orders) throws IOException;
}
