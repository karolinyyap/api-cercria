package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.ControleUsoMedicamento;

@Repository
public interface RepositorioControleMedicamento extends CrudRepository<ControleUsoMedicamento, Integer>{
    List<ControleUsoMedicamento> findByAcolhidoId(Integer id);
}
