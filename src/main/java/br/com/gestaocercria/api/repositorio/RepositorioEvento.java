package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.Evento;

@Repository
public interface RepositorioEvento extends CrudRepository<Evento, Integer>  {
    @Override
    @NonNull
    List<Evento> findAll();
}
