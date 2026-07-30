package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Patrimonio;

public interface RepositorioPatrimonio extends CrudRepository<Patrimonio, Integer>{
    List<Patrimonio> findByExcluidoFalse();

}
