package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.Medicamento;

@Repository
public interface RepositorioMedicamento extends CrudRepository<Medicamento, Integer> {
    
}
