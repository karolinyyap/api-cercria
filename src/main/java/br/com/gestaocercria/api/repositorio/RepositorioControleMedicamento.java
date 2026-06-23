package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.ControleUsoMedicamento;

public interface RepositorioControleMedicamento extends CrudRepository<ControleUsoMedicamento, Integer>{
    List<ControleUsoMedicamento> findByAcolhidoId(Integer id);
}
