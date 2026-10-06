package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.*;
import br.com.gestaocercria.api.repositorio.*;

@RestController
@RequestMapping("/alerta")
public class ControleAlerta {

    private final RepositorioEvento eventoRepo;
    private final RepositorioEstoqueMedicamento estoqueRepo;
    private final RepositorioAgendaMedicamento agendaRepo;
    private final RepositorioEntradaProduto estoqueProdutoRepo;

    public ControleAlerta(
            RepositorioEvento eventoRepo,
            RepositorioEstoqueMedicamento estoqueRepo,
            RepositorioAgendaMedicamento agendaRepo,
            RepositorioEntradaProduto estoqueProdutoRepo) {

        this.eventoRepo = eventoRepo;
        this.estoqueRepo = estoqueRepo;
        this.agendaRepo = agendaRepo;
        this.estoqueProdutoRepo = estoqueProdutoRepo;
    }

    @GetMapping("/listagem")
    public List<Map<String, String>> listarAlertas() {

        List<Map<String, String>> alertas = new ArrayList<>();

        LocalDate hoje = LocalDate.now();
        LocalDate limiteValidade = hoje.plusDays(30);

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ============================================================
        // EVENTOS DOS PRÓXIMOS 7 DIAS
        // ============================================================

        List<Evento> eventos =
                eventoRepo.findByExcluidoFalseAndDataBetween(
                        hoje,
                        hoje.plusDays(7)
                );

        for (Evento e : eventos) {

            if (e.getData() == null) {
                continue;
            }

            Map<String, String> alerta = new HashMap<>();

            alerta.put("tipo", "Evento");

            alerta.put(
                "mensagem",
                "O evento "
                + e.getNome()
                + " acontecerá em "
                + e.getData().format(formato)
            );

            alertas.add(alerta);
        }

        // ============================================================
        // ESTOQUE BAIXO
        // ============================================================

        List<EstoqueMedicamento> estoquesBaixos =
                estoqueRepo.findByQuantidadeAtualLessThanEqual(10.0);

        for (EstoqueMedicamento e : estoquesBaixos) {

            if (e.getMedicamento() == null) {
                continue;
            }

            Map<String, String> alerta = new HashMap<>();

            alerta.put("tipo", "Estoque Baixo");

            alerta.put(
                "mensagem",
                "O medicamento "
                + e.getMedicamento().getNome()
                + " possui apenas "
                + e.getQuantidadeAtual()
                + " unidades em estoque."
            );

            alertas.add(alerta);
        }

        // ============================================================
        // MEDICAMENTOS PENDENTES DE HOJE
        // ============================================================

        List<AgendaMedicamento> agendas =
                agendaRepo.findByDataAndStatus(
                        hoje.toString(),
                        "PENDENTE"
                );

        for (AgendaMedicamento a : agendas) {

            if (a.getAcolhido() == null ||
                a.getMedicamento() == null) {
                continue;
            }

            Map<String, String> alerta = new HashMap<>();

            alerta.put("tipo", "Medicamento");

            String horario = a.getHorario();

            if (horario != null && horario.length() >= 5) {
                horario = horario.substring(0, 5);
            }

            alerta.put(
                "mensagem",
                a.getAcolhido().getNome()
                + " deve tomar "
                + a.getMedicamento().getNome()
                + " às "
                + (horario != null ? horario : "--:--")
            );

            alertas.add(alerta);
        }

        // ============================================================
        // MEDICAMENTOS A VENCER EM ATÉ 30 DIAS
        // ============================================================

        String hojeString = hoje.toString();
        String limiteValidadeString = limiteValidade.toString();

        List<EstoqueMedicamento> estoquesComValidade =
        estoqueRepo.findByDataValidadeBetween(
                hojeString,
                limiteValidadeString
        );

        for (EstoqueMedicamento e : estoquesComValidade) {

            if (e.getMedicamento() == null) {
                continue;
            }

            try {

                LocalDate validade =
                        LocalDate.parse(e.getDataValidade());

                if (!validade.isBefore(hoje)
                        && !validade.isAfter(limiteValidade)) {

                    Map<String, String> alerta =
                            new HashMap<>();

                    alerta.put("tipo", "Validade");

                    alerta.put(
                        "mensagem",
                        "O medicamento "
                        + e.getMedicamento().getNome()
                        + " vence em "
                        + validade.format(formato)
                    );

                    alertas.add(alerta);
                }

            } catch (Exception ex) {

                System.out.println(
                    "Data de validade de medicamento inválida: "
                    + e.getDataValidade()
                );
            }
        }

        // ============================================================
        // PRODUTOS A VENCER EM ATÉ 30 DIAS
        // ============================================================

        List<EntradaProduto> produtosComValidade =
                estoqueProdutoRepo.findByDataValidadeBetween(
                        hojeString,
                        limiteValidadeString
                );

        for (EntradaProduto p : produtosComValidade) {

            if (p.getProduto() == null) {
                continue;
            }

            try {

                LocalDate validade =
                        LocalDate.parse(p.getDataValidade());

                if (!validade.isBefore(hoje)
                        && !validade.isAfter(limiteValidade)) {

                    Map<String, String> alerta =
                            new HashMap<>();

                    alerta.put("tipo", "Validade");

                    alerta.put(
                        "mensagem",
                        "O produto "
                        + p.getProduto().getNome()
                        + " vence em "
                        + validade.format(formato)
                    );

                    alertas.add(alerta);
                }

            } catch (Exception ex) {

                System.out.println(
                    "Data de validade de produto inválida: "
                    + p.getDataValidade()
                );
            }
        }

        return alertas;
    }
}