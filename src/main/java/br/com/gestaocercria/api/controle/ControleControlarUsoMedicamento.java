package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.*;
import br.com.gestaocercria.api.repositorio.*;

@RestController
@RequestMapping("/controle-medicamento")
public class ControleControlarUsoMedicamento {

    private final RepositorioControleMedicamento acao;
    private final RepositorioAgendaMedicamento agendaRepo;
    private final RepositorioEstoqueMedicamento estoqueRepo;

    ControleControlarUsoMedicamento(RepositorioControleMedicamento acao, RepositorioAgendaMedicamento agendaRepo, RepositorioEstoqueMedicamento estoqueRepo) {
        this.acao = acao;
        this.agendaRepo = agendaRepo;
        this.estoqueRepo = estoqueRepo;
    }

    @PostMapping("/cadastro")
    public ControleUsoMedicamento cadastrar(@RequestBody @NonNull ControleUsoMedicamento controle) {
        EstoqueMedicamento estoque = estoqueRepo.findTopByMedicamentoIdOrderByDataEntradaDesc(controle.getMedicamento().getId());

        if (estoque == null) {
            throw new RuntimeException("Medicamento sem estoque.");
        }

        int quantidadeNecessaria = (int) (calcularQuantidadeDoses(controle) * controle.getDose());

        if (quantidadeNecessaria > estoque.getQuantidade_atual()) {
            throw new RuntimeException("Estoque insuficiente. Necessário: "
                + quantidadeNecessaria + " | Disponível: " + estoque.getQuantidade_atual());
        }

        ControleUsoMedicamento salvo = acao.save(controle);
        gerarAgenda(salvo);

        return salvo;
    }

    @GetMapping("/listagem")
    public Iterable<ControleUsoMedicamento> selecionar() {
        return acao.findAll();
    }

    @GetMapping("/{id}")
    public ControleUsoMedicamento buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public ControleUsoMedicamento editar(@RequestBody @NonNull ControleUsoMedicamento controle) {
        return acao.save(controle);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }

    @GetMapping("/acolhido/{id}")
    public List<ControleUsoMedicamento> listarPorAcolhido(@PathVariable Integer id) {
        return acao.findByAcolhidoId(id);
    }


    private void gerarAgenda(ControleUsoMedicamento controle) {
        LocalDate dia = LocalDate.parse(controle.getDataInicio());
        LocalDate fim = Boolean.TRUE.equals(controle.getUsoContinuo())?dia.plusMonths(1):LocalDate.parse(controle.getDataFim());
        List<String> dias = controle.getDiasSemana() != null?List.of(controle.getDiasSemana().split(",")):List.of();
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

                if (controle.getIntervalo() != null) {

                    LocalTime hora = LocalTime.parse(controle.getIniciandoEm());

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
                        agenda.setFuncionarioResponsavel(controle.getFuncionarioCadastro());

                        agendaRepo.save(agenda);

                        hora = hora.plusHours(controle.getIntervalo());
                    }

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
                    agenda.setFuncionarioResponsavel(controle.getFuncionarioCadastro());

                    agendaRepo.save(agenda);
                }
            }
            dia = dia.plusDays(1);
        }
    }

    private int calcularQuantidadeDoses(ControleUsoMedicamento controle) {

        LocalDate dia = LocalDate.parse(controle.getDataInicio());

        LocalDate fim = Boolean.TRUE.equals(controle.getUsoContinuo())
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
                if (controle.getIntervalo() != null) {
                    total += controle.getVezesAoDia();
                } else {
                    total++;
                }
            }

            dia = dia.plusDays(1);
        }

        return total;
    }
}