package br.com.gestaocercria.api.repositorio;

import org.springframework.data.repository.CrudRepository;
import br.com.gestaocercria.api.entidade.Produto;

public interface RepositorioProduto extends CrudRepository<Produto, Integer>{
    
}
