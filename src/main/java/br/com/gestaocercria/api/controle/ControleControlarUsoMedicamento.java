package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.gestaocercria.api.entidade.*;
import br.com.gestaocercria.api.repositorio.*;

@RestController
@RequestMapping("/controle-medicamento")
public class ControleControlarUsoMedicamento {

    private final RepositorioControleMedicamento acao;
    private final RepositorioAgendaMedicamento agendaRepo;
    private final RepositorioEstoqueMedicamento estoqueRepo;

    ControleControlarUsoMedicamento(
            RepositorioControleMedicamento acao,
            RepositorioAgendaMedicamento agendaRepo,
            RepositorioEstoqueMedicamento estoqueRepo) {

        this.acao = acao;
        this.agendaRepo = agendaRepo;
        this.estoqueRepo = estoqueRepo;
    }

    @PostMapping("/cadastro")
    public ControleUsoMedicamento cadastrar(
            @RequestBody @NonNull ControleUsoMedicamento controle,
            @RequestParam(defaultValue = "false") boolean confirmarSemEstoque) {

        EstoqueMedicamento estoque =
                estoqueRepo.findTopByMedicamentoIdOrderByDataEntradaDesc(
                        controle.getMedicamento().getId()
                );

        int quantidadeNecessaria =
                (int) (calcularQuantidadeDoses(controle) * controle.getDose());

        double quantidadeDisponivel = 0;

        if (estoque != null && estoque.getQuantidadeAtual() != null) {
            quantidadeDisponivel = estoque.getQuantidadeAtual();
        }

        /*
         * Verifica se existe quantidade suficiente no estoque.
         *
         * Se não existir e o usuário ainda não confirmou,
         * retorna 409 para o Angular abrir a confirmação.
         */
        if (quantidadeNecessaria > quantidadeDisponivel
                && !confirmarSemEstoque) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não há quantidade disponível em estoque para o período informado."
            );
        }

        /*
         * Se chegou aqui:
         * - existe estoque suficiente
         * OU
         * - o usuário confirmou que deseja cadastrar mesmo sem estoque.
         */
        ControleUsoMedicamento salvo = acao.save(controle);

        gerarAgenda(salvo);

        return salvo;
    }

    @GetMapping("/listagem")
    public Iterable<ControleUsoMedicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")
    public ControleUsoMedicamento buscarPorId(
            @PathVariable @NonNull Integer id) {

        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public ControleUsoMedicamento editar(
            @RequestBody @NonNull ControleUsoMedicamento controle) {

        return acao.save(controle);
    }

    @DeleteMapping("/{id}")
    public void remover(
            @PathVariable @NonNull Integer id) {

        acao.deleteById(id);
    }

    @GetMapping("/acolhido/{id}")
    public List<ControleUsoMedicamento> listarPorAcolhido(
            @PathVariable Integer id) {

        return acao.findByAcolhidoId(id);
    }


    private void gerarAgenda(ControleUsoMedicamento controle) {

        LocalDate dia = LocalDate.parse(controle.getDataInicio());

        LocalDate fim =
                Boolean.TRUE.equals(controle.getUsoContinuo())
                        ? dia.plusMonths(1)
                        : LocalDate.parse(controle.getDataFim());

        List<String> dias =
                controle.getDiasSemana() != null
                        ? List.of(controle.getDiasSemana().split(","))
                        : List.of();

        while (!dia.isAfter(fim)) {

            String diaAtual = switch (dia.getDayOfWeek()) {
                case MONDAY -> "Segunda";
                case TUESDAY -> "Terça";
                case WEDNESDAY -> "Quarta";
                case THURSDAY -> "Quinta";
                case FRIDAY -> "Sexta";
                case SATURDAY -> "Sábado";
                case SUNDAY -> "Domingo";
            };

            if (dias.isEmpty() || dias.contains(diaAtual)) {

                /*
                 * FREQUÊNCIA POR INTERVALO
                 */
                if (controle.getIntervalo() != null) {

                    LocalTime hora =
                            LocalTime.parse(controle.getIniciandoEm());

                    for (int i = 0; i < controle.getVezesAoDia(); i++) {

                        AgendaMedicamento agenda = new AgendaMedicamento();

                        agenda.setData(dia.toString());
                        agenda.setHorario(hora.toString());
                        agenda.setDose(controle.getDose());
                        agenda.setStatus("PENDENTE");
                        agenda.setObservacao(controle.getObservacao());
                        agenda.setAcolhido(controle.getAcolhido());
                        agenda.setMedicamento(controle.getMedicamento());
                        agenda.setControleUso(controle);
                        agenda.setFuncionarioResponsavel(
                                controle.getFuncionarioCadastro()
                        );

                        agendaRepo.save(agenda);

                        hora = hora.plusHours(controle.getIntervalo());
                    }

                /*
                 * HORÁRIO FIXO
                 */
                } else {

                    AgendaMedicamento agenda = new AgendaMedicamento();

                    agenda.setData(dia.toString());
                    agenda.setHorario(controle.getHorarioFixo());
                    agenda.setDose(controle.getDose());
                    agenda.setStatus("PENDENTE");
                    agenda.setObservacao(controle.getObservacao());
                    agenda.setAcolhido(controle.getAcolhido());
                    agenda.setMedicamento(controle.getMedicamento());
                    agenda.setControleUso(controle);
                    agenda.setFuncionarioResponsavel(
                            controle.getFuncionarioCadastro()
                    );

                    agendaRepo.save(agenda);
                }
            }

            dia = dia.plusDays(1);
        }
    }


    private int calcularQuantidadeDoses(
            ControleUsoMedicamento controle) {

        LocalDate dia =
                LocalDate.parse(controle.getDataInicio());

        LocalDate fim =
                Boolean.TRUE.equals(controle.getUsoContinuo())
                        ? dia.plusMonths(1)
                        : LocalDate.parse(controle.getDataFim());

        List<String> dias =
                controle.getDiasSemana() != null
                        ? List.of(controle.getDiasSemana().split(","))
                        : List.of();

        int total = 0;

        while (!dia.isAfter(fim)) {

            String diaAtual = switch (dia.getDayOfWeek()) {
                case MONDAY -> "Segunda";
                case TUESDAY -> "Terça";
                case WEDNESDAY -> "Quarta";
                case THURSDAY -> "Quinta";
                case FRIDAY -> "Sexta";
                case SATURDAY -> "Sábado";
                case SUNDAY -> "Domingo";
            };

            if (dias.isEmpty() || dias.contains(diaAtual)) {

                /*
                 * Intervalo:
                 * usa a quantidade de vezes ao dia.
                 */
                if (controle.getIntervalo() != null) {
                    total += controle.getVezesAoDia();

                /*
                 * Horário fixo:
                 * uma dose por dia.
                 */
                } else {
                    total++;
                }
            }

            dia = dia.plusDays(1);
        }

        return total;
    }
}