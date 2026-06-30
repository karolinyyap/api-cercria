package br.com.gestaocercria.api.controle;

import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.SaidaProduto;
import br.com.gestaocercria.api.repositorio.RepositorioSaidaProduto;
import lombok.NonNull;

@RestController
@RequestMapping("/controle-produto/saida")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleSaidaProduto {
    private final RepositorioSaidaProduto acao;

    public ControleSaidaProduto(RepositorioSaidaProduto acao){
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public SaidaProduto cadastrar(@RequestBody @NonNull SaidaProduto p) {
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<SaidaProduto> selecionar() {
        return acao.findAll();
    }
}
