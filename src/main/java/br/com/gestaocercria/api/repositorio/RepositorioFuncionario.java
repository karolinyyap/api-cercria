package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Funcionario;

public interface RepositorioFuncionario extends CrudRepository<Funcionario, Integer> {
    Funcionario findByEmail(String email);
}
