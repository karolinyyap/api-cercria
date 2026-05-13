package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.gestaocercria.api.entidade.Funcionario;

@Repository
public interface RepositorioFuncionario extends CrudRepository<Funcionario, Integer> {
    Funcionario findByEmail(String email);
}
