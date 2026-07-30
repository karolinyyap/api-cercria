package br.com.gestaocercria.api.controle;

import org.springframework.lang.NonNull;

import br.com.gestaocercria.api.entidade.Medicamento;
import br.com.gestaocercria.api.repositorio.RepositorioMedicamento;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicamento")
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
        return acao.findByExcluidoFalse();
    }

    @GetMapping("/{id}")            
    public Medicamento buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Medicamento editar(@RequestBody @NonNull Medicamento m) {
        return acao.save(m);
    }

    @PutMapping("/excluir/{id}")
    public Medicamento excluir(@PathVariable Integer id) {

        Medicamento med = acao.findById(id)
            .orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));

        med.setExcluido(true);

        return acao.save(med);
    }
}
