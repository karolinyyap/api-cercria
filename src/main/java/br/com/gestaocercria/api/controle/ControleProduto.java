package br.com.gestaocercria.api.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Produto;
import br.com.gestaocercria.api.repositorio.RepositorioProduto;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/produto")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleProduto {
    @Autowired
    private RepositorioProduto acao;

    @PostMapping("/cadastro")
    public Produto cadastrar(@RequestBody Produto p) {
        return acao.save(p);
    }

    @GetMapping("/listagem")
    public Iterable<Produto> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Produto buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Produto editar(@RequestBody Produto p) {
        return acao.save(p);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        acao.deleteById(id);
    }
}

