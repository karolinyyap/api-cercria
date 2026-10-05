package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Acolhido;

public interface RepositorioAcolhido extends CrudRepository<Acolhido, Integer>{
    List<Acolhido> findByExcluidoFalseOrderByNomeAsc();
}
