package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.Evento;

@Repository
public interface RepositorioEvento extends CrudRepository<Evento, Integer>  {
    
}
