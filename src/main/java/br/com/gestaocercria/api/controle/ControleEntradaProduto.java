package br.com.gestaocercria.api.controle;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import br.com.gestaocercria.api.entidade.EntradaProduto;
import br.com.gestaocercria.api.repositorio.RepositorioEntradaProduto;

import lombok.NonNull;

@RestController
@RequestMapping("/controle-produto/entrada")
public class ControleEntradaProduto {

    private final RepositorioEntradaProduto acao;

    public ControleEntradaProduto(RepositorioEntradaProduto acao){
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public EntradaProduto cadastrar(@RequestBody @NonNull EntradaProduto p) {
        p.setQuantidadeAtual(p.getQuantidade());
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<EntradaProduto> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/produto/{id}")
    public List<EntradaProduto> listarPorProduto(@PathVariable Integer id) {
        return acao.findByProdutoId(id);
    }
    
}
