package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.*;
import br.com.gestaocercria.api.repositorio.*;

@RestController
@RequestMapping("/alerta")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleAlerta {

    private final RepositorioEvento eventoRepo;
    private final RepositorioEstoqueMedicamento estoqueRepo;

    public ControleAlerta(
        RepositorioEvento eventoRepo,
        RepositorioEstoqueMedicamento estoqueRepo
    ) {
        this.eventoRepo = eventoRepo;
        this.estoqueRepo = estoqueRepo;
    }

    @GetMapping("/listagem")
    public List<Map<String, String>> listarAlertas() {

        List<Map<String, String>> alertas = new ArrayList<>();

        LocalDate hoje = LocalDate.now();

        // EVENTOS DOS PRÓXIMOS 3 DIAS
        List<Evento> eventos = (List<Evento>) eventoRepo.findAll();

        eventos.stream()
            .filter(e ->
                !e.getData().isBefore(hoje)
                && !e.getData().isAfter(hoje.plusDays(3))
            )
            .forEach(e -> {

                Map<String, String> alerta = new HashMap<>();

                alerta.put("tipo", "Evento");

                alerta.put(
                    "mensagem",
                    "O evento "
                    + e.getNome()
                    + " acontecerá em "
                    + e.getData().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                );

                alertas.add(alerta);
            });

        // ESTOQUE BAIXO
        List<EstoqueMedicamento> estoques =
            (List<EstoqueMedicamento>) estoqueRepo.findAll();

        estoques.stream()
            .filter(e -> e.getQuantidade_atual() <= 10)
            .forEach(e -> {

                Map<String, String> alerta = new HashMap<>();

                alerta.put("tipo", "Estoque Baixo");

                alerta.put(
                    "mensagem",
                    "O medicamento "
                    + e.getMedicamento().getNome()
                    + " possui apenas "
                    + e.getQuantidade_atual()
                    + " unidades em estoque."
                );

                alertas.add(alerta);
            });

        return alertas;
    }
}
