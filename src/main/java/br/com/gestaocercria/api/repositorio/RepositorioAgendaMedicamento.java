package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.AgendaMedicamento;

public interface RepositorioAgendaMedicamento extends CrudRepository<AgendaMedicamento, Long> {

    List<AgendaMedicamento> findByAcolhidoId(Integer id);

    List<AgendaMedicamento> findByDataAndStatus(
        String data,
        String status
    );
}