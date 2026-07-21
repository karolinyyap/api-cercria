package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.Evento;
import br.com.gestaocercria.api.repositorio.RepositorioEvento;


@RestController
@RequestMapping("/evento")
public class ControleEvento {
    private final RepositorioEvento acao;


    ControleEvento(RepositorioEvento acao) {
        this.acao = acao;
    }

    @PostMapping("/cadastro")
    public Evento cadastrar(@RequestBody @NonNull Evento e) {
        return acao.save(e);
    }

    @GetMapping("/listagem")
    public Iterable<Evento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")            
    public Evento buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Evento editar(@RequestBody @NonNull Evento e) {
        return acao.save(e);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }

    @GetMapping("/proximos")
    public List<Evento> proximosEventos() {

        LocalDate hoje = LocalDate.now();
        List<Evento> eventos = (List<Evento>) acao.findAll();
        return eventos.stream()
            .filter(e -> {
                LocalDate dataEvento = e.getData();
                return !dataEvento.isBefore(hoje)
                    && !dataEvento.isAfter(hoje.plusDays(3));
            })
            .toList();
    }
}
