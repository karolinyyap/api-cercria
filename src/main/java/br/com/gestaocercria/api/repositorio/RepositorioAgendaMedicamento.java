package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.AgendaMedicamento;

@Repository
public interface RepositorioAgendaMedicamento
extends CrudRepository<AgendaMedicamento, Long> {

    List<AgendaMedicamento> findByAcolhidoId(Integer id);

    
}