package br.com.gestaocercria.api.controle;

import java.util.List;

import org.springframework.lang.NonNull;

import br.com.gestaocercria.api.entidade.EstoqueMedicamento;
import br.com.gestaocercria.api.repositorio.RepositorioEstoqueMedicamento;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/entrada-medicamento")
public class ControleEstoqueMedicamento {

    private final RepositorioEstoqueMedicamento acao;

    ControleEstoqueMedicamento(RepositorioEstoqueMedicamento acao) {
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public EstoqueMedicamento cadastrar(@RequestBody EstoqueMedicamento e) {
        List<EstoqueMedicamento> entradas = acao.findByMedicamentoId(e.getMedicamento().getId());

        Double estoqueAtual = 0.0;

        if (!entradas.isEmpty()) {
            EstoqueMedicamento ultima = entradas.get(entradas.size() - 1);
            estoqueAtual = ultima.getQuantidadeAtual();
        }

        e.setQuantidadeAtual(estoqueAtual + e.getQuantidade());

        return acao.save(e);
    }

    @GetMapping("/listagem")
    public Iterable<EstoqueMedicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")
    public EstoqueMedicamento buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @GetMapping("/medicamento/{id}")
    public List<EstoqueMedicamento> listarPorMedicamento(@PathVariable Integer id) {
        return acao.findByMedicamentoId(id);
    }

    @PutMapping("/edicao")
    public EstoqueMedicamento editar(@RequestBody @NonNull EstoqueMedicamento e) {
        return acao.save(e);
    }

}