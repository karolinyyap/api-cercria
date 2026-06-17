package br.com.gestaocercria.api.controle;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Medicamento;
import br.com.gestaocercria.api.repositorio.RepositorioMedicamento;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicamento")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleMedicamento {
    private final RepositorioMedicamento acao;

    ControleMedicamento(RepositorioMedicamento acao) {
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public Medicamento cadastrar(@RequestBody @NonNull Medicamento m) {
        return acao.save(m);
    }

    @GetMapping("/listagem")
    public Iterable<Medicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Medicamento buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Medicamento editar(@RequestBody @NonNull Medicamento m) {
        return acao.save(m);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }
}
