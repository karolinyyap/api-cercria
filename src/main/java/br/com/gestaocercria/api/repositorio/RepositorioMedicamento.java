package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.gestaocercria.api.entidade.Medicamento;

public interface RepositorioMedicamento extends CrudRepository<Medicamento, Integer> {
    List<Medicamento> findByExcluidoFalseOrderByNomeAsc();
}
