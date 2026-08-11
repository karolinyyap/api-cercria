package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.gestaocercria.api.entidade.SaidaProduto;

public interface RepositorioSaidaProduto extends CrudRepository<SaidaProduto, Integer>{
    List<SaidaProduto> findByProdutoId(Integer produtoId);

}
