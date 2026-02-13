package fr.sqq.achatgroupe.application.port.in;

import fr.sqq.achatgroupe.domain.model.vente.Vente;

import java.util.List;

public interface ListVentesUseCase {

    List<Vente> listActiveVentes();
}
