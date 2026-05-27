package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.EstoqueMedicamento;

@Repository
public interface RepositorioEstoqueMedicamento extends CrudRepository<EstoqueMedicamento, Integer> {
    List<EstoqueMedicamento> findByMedicamentoId(Integer medicamentoId);

    EstoqueMedicamento findTopByMedicamentoIdOrderByDataEntradaDesc (Integer id);
}
