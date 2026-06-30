package br.com.gestaocercria.api.controle;

import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.EntradaProduto;
import br.com.gestaocercria.api.repositorio.RepositorioEntradaProduto;
import lombok.NonNull;

@RestController
@RequestMapping("/controle-produto/entrada")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleEntradaProduto {

    private final RepositorioEntradaProduto acao;

    public ControleEntradaProduto(RepositorioEntradaProduto acao){
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public EntradaProduto cadastrar(@RequestBody @NonNull EntradaProduto p) {
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<EntradaProduto> selecionar() {
        return acao.findAll();
    }
}
