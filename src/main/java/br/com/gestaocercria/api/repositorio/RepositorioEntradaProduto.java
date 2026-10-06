package br.com.gestaocercria.api.repositorio;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.gestaocercria.api.entidade.EntradaProduto;

public interface RepositorioEntradaProduto extends CrudRepository<EntradaProduto, Integer>{
    List<EntradaProduto> findByProdutoIdAndQuantidadeAtualGreaterThanOrderByDataEntradaAsc(
        Integer produtoId,
        Double quantidadeAtual);

    List<EntradaProduto> findByProdutoId(Integer produtoId);

    List<EntradaProduto> findByDataValidadeBetween(
        String inicio,
        String fim
    );
}
