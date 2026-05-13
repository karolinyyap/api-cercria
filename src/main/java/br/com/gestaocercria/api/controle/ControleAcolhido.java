package br.com.gestaocercria.api.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Acolhido;
import br.com.gestaocercria.api.repositorio.RepositorioAcolhido;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/acolhido")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleAcolhido {
    @Autowired
    private RepositorioAcolhido acao;

    @PostMapping("/cadastro")
    public Acolhido cadastrar(@RequestBody Acolhido a) {
        return acao.save(a);
    }

    @GetMapping("/listagem")
    public Iterable<Acolhido> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Acolhido buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Acolhido editar(@RequestBody Acolhido a) {
        return acao.save(a);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        acao.deleteById(id);
    }
}
