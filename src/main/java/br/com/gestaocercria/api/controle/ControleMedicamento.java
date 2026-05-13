package br.com.gestaocercria.api.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Medicamento;
import br.com.gestaocercria.api.repositorio.RepositorioMedicamento;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/medicamento")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleMedicamento {
    @Autowired
    private RepositorioMedicamento acao;

    @PostMapping("/cadastro")
    public Medicamento cadastrar(@RequestBody Medicamento m) {
        return acao.save(m);
    }

    @GetMapping("/listagem")
    public Iterable<Medicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Medicamento buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Medicamento editar(@RequestBody Medicamento m) {
        return acao.save(m);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        acao.deleteById(id);
    }
}
