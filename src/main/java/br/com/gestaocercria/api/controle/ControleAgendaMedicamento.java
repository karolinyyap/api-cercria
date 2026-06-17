package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.*;
import br.com.gestaocercria.api.repositorio.*;

@RestController
@RequestMapping("/controle-medicamento/agenda")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleAgendaMedicamento {

        private final RepositorioEstoqueMedicamento estoqueRepo;
        private final RepositorioAgendaMedicamento agendaRepo;
        private final RepositorioAgendaMedicamento acao;

        ControleAgendaMedicamento(RepositorioEstoqueMedicamento estoqueRepo, RepositorioAgendaMedicamento agendaRepo, RepositorioAgendaMedicamento acao) {
                this.estoqueRepo = estoqueRepo;
                this.agendaRepo = agendaRepo;
                this.acao = acao;
        }

        @GetMapping("/listagem")
        public Iterable<AgendaMedicamento> selecionar() {
                return acao.findAll();
        }

        @GetMapping("/{id}")
        public AgendaMedicamento buscarPorId(@PathVariable @NonNull Long id) {
                return acao.findById(id).orElse(null);
        }

        @GetMapping("/acolhido/{id}")
        public List<AgendaMedicamento> listarPorAcolhido(@PathVariable Integer id) {
                return acao.findByAcolhidoId(id);
        }

        @PutMapping("/tomou/{id}")
        public AgendaMedicamento confirmarDose(@PathVariable @NonNull Long id) {

                AgendaMedicamento agenda =agendaRepo.findById(id).orElse(null);

                if (agenda == null) {
                        throw new RuntimeException(
                        "Agenda não encontrada"
                        );
                }

                EstoqueMedicamento estoque = estoqueRepo.findTopByMedicamentoIdOrderByDataEntradaDesc(agenda
                        .getMedicamento().getId());

                if (estoque == null) {
                        throw new RuntimeException("Medicamento sem estoque");
                }

                if (estoque.getQuantidade_atual() < agenda.getDose()) {
                        throw new RuntimeException("Estoque insuficiente");
                }

                estoque.setQuantidade_atual(estoque.getQuantidade_atual() - agenda.getDose());

                estoqueRepo.save(estoque);
                agenda.setEstoqueMedicamento(estoque);
                agenda.setStatus("DADO");
                agenda.setDataBaixa(LocalDate.now().toString());

                return agendaRepo.save(agenda);
                
        }


        @PutMapping("/nao-tomou/{id}")
        public AgendaMedicamento naoTomou(@PathVariable @NonNull Long id,@RequestBody String motivo) {
                AgendaMedicamento agenda = agendaRepo.findById(id).orElse(null);

                if (agenda == null) {
                        return null;
                }

                agenda.setStatus("NAO_TOMOU");
                agenda.setMotivoNaoTomou(motivo);
                agenda.setDataBaixa(LocalDate.now().toString());

                return agendaRepo.save(agenda);
        }

        @PostMapping("/esporadico")
        public AgendaMedicamento salvarEsporadico(@RequestBody AgendaMedicamento agenda) {

                EstoqueMedicamento estoque = estoqueRepo.findTopByMedicamentoIdOrderByDataEntradaDesc(agenda.getMedicamento().getId());

                if (estoque == null) {
                        throw new RuntimeException("Medicamento sem estoque");
                }

                if (estoque.getQuantidade_atual() < agenda.getDose()) {
                        throw new RuntimeException("Estoque insuficiente");
                }

                estoque.setQuantidade_atual(estoque.getQuantidade_atual() - agenda.getDose());

                estoqueRepo.save(estoque);
                agenda.setEstoqueMedicamento(estoque);
                agenda.setStatus("DADO");
                agenda.setDataBaixa(LocalDate.now().toString());

                return agendaRepo.save(agenda);
        }
        
        @GetMapping("/estoque-baixo")
        public List<EstoqueMedicamento> estoqueBaixo() {
                List<EstoqueMedicamento> estoques = (List<EstoqueMedicamento>) estoqueRepo.findAll();
                return estoques.stream().filter(e -> e.getQuantidade_atual() <= 10).toList();
        }
}