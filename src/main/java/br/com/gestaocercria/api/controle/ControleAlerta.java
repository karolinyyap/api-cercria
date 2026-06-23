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
    private final RepositorioAgendaMedicamento agendaRepo;

    public ControleAlerta(RepositorioEvento eventoRepo, RepositorioEstoqueMedicamento estoqueRepo, RepositorioAgendaMedicamento agendaRepo) {
        this.eventoRepo = eventoRepo;
        this.estoqueRepo = estoqueRepo;
        this.agendaRepo = agendaRepo;
    }

    @GetMapping("/listagem")
    public List<Map<String, String>> listarAlertas() {
        List<Map<String, String>> alertas = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        // EVENTOS DOS PRÓXIMOS 3 DIAS
        List<Evento> eventos = (List<Evento>) eventoRepo.findAll();

        eventos.stream().filter(e ->!e.getData().isBefore(hoje) && !e.getData().isAfter(hoje.plusDays(3)))
            .forEach(e -> {
                Map<String, String> alerta = new HashMap<>();
                alerta.put("tipo", "Evento");
                alerta.put("mensagem", "O evento " + e.getNome() + " acontecerá em " + e.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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

                alerta.put("mensagem", "O medicamento " + e.getMedicamento().getNome()
                    + " possui apenas " + e.getQuantidade_atual() + " unidades em estoque.");

                alertas.add(alerta);
            });

        // MEDICAMENTOS PENDENTES DE HOJE
        String hojeTexto = LocalDate.now().toString();

        List<AgendaMedicamento> agendas = (List<AgendaMedicamento>) agendaRepo.findAll();

        agendas.stream().filter(a -> hojeTexto.equals(a.getData()) && "PENDENTE".equals(a.getStatus()))
            .forEach(a -> {
                Map<String, String> alerta = new HashMap<>();
                alerta.put("tipo", "Medicamento");
                alerta.put("mensagem", a.getAcolhido().getNome() + " deve tomar "
                    + a.getMedicamento().getNome() + " às " + a.getHorario().substring(0, 5));

                alertas.add(alerta);
            });

        return alertas;
    }
}
