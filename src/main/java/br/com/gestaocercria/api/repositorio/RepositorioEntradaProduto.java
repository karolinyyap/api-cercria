package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;

import br.com.gestaocercria.api.entidade.EntradaProduto;

public interface RepositorioEntradaProduto extends CrudRepository<EntradaProduto, Integer>{
    
}
