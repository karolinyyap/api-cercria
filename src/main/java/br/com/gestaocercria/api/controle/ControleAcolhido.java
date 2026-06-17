package br.com.gestaocercria.api.controle;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Acolhido;
import br.com.gestaocercria.api.repositorio.RepositorioAcolhido;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/acolhido")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleAcolhido {
    private final RepositorioAcolhido acao;

    ControleAcolhido(RepositorioAcolhido acao) {
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public Acolhido cadastrar(@RequestBody @NonNull Acolhido a) {
        return acao.save(a);
    }

    @GetMapping("/listagem")
    public Iterable<Acolhido> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Acolhido buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Acolhido editar(@RequestBody @NonNull Acolhido a) {
        return acao.save(a);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }
}
