package fr.sqq.achatgroupe.application.port.out;

import fr.sqq.achatgroupe.application.query.GenerateDistributionListQuery.DistributionOrder;

import java.io.IOException;
import java.util.List;

public interface DistributionPdfGenerator {
    byte[] generate(List<DistributionOrder> orders) throws IOException;
}
