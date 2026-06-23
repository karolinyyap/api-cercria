package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.EstoqueMedicamento;

public interface RepositorioEstoqueMedicamento extends CrudRepository<EstoqueMedicamento, Integer> {
    List<EstoqueMedicamento> findByMedicamentoId(Integer medicamentoId);

    EstoqueMedicamento findTopByMedicamentoIdOrderByDataEntradaDesc (Integer id);
}
