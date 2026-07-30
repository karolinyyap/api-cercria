package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import br.com.gestaocercria.api.entidade.Evento;

public interface RepositorioEvento extends CrudRepository<Evento, Integer>  {
    @Override
    @NonNull
    List<Evento> findAll();
    List<Evento> findByExcluidoFalse();

}
