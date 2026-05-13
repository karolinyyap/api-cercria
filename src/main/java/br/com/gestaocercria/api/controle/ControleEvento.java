package br.com.gestaocercria.api.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Evento;
import br.com.gestaocercria.api.repositorio.RepositorioEvento;


@RestController
@RequestMapping("/evento")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleEvento {
    @Autowired
    private RepositorioEvento acao;

    @PostMapping("/cadastro")
    public Evento cadastrar(@RequestBody Evento e) {
        return acao.save(e);
    }

    @GetMapping("/listagem")
    public Iterable<Evento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Evento buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Evento editar(@RequestBody Evento e) {
        return acao.save(e);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        acao.deleteById(id);
    }
}
