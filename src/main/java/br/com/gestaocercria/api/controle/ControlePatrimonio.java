package br.com.gestaocercria.api.controle;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import br.com.gestaocercria.api.entidade.Patrimonio;
import br.com.gestaocercria.api.repositorio.RepositorioPatrimonio;

@RestController
@RequestMapping("/patrimonio")
public class ControlePatrimonio {
    private final RepositorioPatrimonio acao;

    ControlePatrimonio (RepositorioPatrimonio acao){
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public Patrimonio cadastrar(@RequestBody @NonNull Patrimonio p) {
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<Patrimonio> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Patrimonio buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Patrimonio editar(@RequestBody @NonNull Patrimonio p) {
        return acao.save(p);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }
}
