package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Medicamento;

public interface RepositorioMedicamento extends CrudRepository<Medicamento, Integer> {
    
}
