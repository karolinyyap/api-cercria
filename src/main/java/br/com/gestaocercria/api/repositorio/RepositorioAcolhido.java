package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.Acolhido;

@Repository
public interface RepositorioAcolhido extends CrudRepository<Acolhido, Integer>{
    
}
