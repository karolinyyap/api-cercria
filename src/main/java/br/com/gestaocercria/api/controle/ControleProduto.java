package br.com.gestaocercria.api.controle;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Produto;
import br.com.gestaocercria.api.repositorio.RepositorioProduto;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produto")
public class ControleProduto {
    private final RepositorioProduto acao;

    ControleProduto(RepositorioProduto acao) {
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public Produto cadastrar(@RequestBody @NonNull Produto p) {
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<Produto> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Produto buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Produto editar(@RequestBody @NonNull Produto p) {
        return acao.save(p);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }
}

