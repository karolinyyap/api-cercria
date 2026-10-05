package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Funcionario;

public interface RepositorioFuncionario extends CrudRepository<Funcionario, Integer> {
    Funcionario findByEmail(String email);
    List<Funcionario> findByExcluidoFalseOrderByNomeAsc();
    
}
