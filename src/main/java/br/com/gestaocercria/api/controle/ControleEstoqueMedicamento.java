package br.com.gestaocercria.api.controle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.EstoqueMedicamento;
import br.com.gestaocercria.api.repositorio.RepositorioEstoqueMedicamento;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/entrada-medicamento")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleEstoqueMedicamento {

    @Autowired
    private RepositorioEstoqueMedicamento acao;

    @PostMapping("/cadastro")
    public EstoqueMedicamento cadastrar(@RequestBody EstoqueMedicamento e) {
        return acao.save(e);
    }

    @GetMapping("/listagem")
    public Iterable<EstoqueMedicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")
    public EstoqueMedicamento buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @GetMapping("/medicamento/{id}")
    public List<EstoqueMedicamento> listarPorMedicamento(@PathVariable Integer id) {
        return acao.findByMedicamentoId(id);
    }

    @PutMapping("/edicao")
    public EstoqueMedicamento editar(@RequestBody EstoqueMedicamento e) {
        return acao.save(e);
    }
}