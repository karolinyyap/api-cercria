package br.com.gestaocercria.api.controle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.AgendaMedicamento;
import br.com.gestaocercria.api.entidade.EstoqueMedicamento;
import br.com.gestaocercria.api.repositorio.RepositorioAgendaMedicamento;
import br.com.gestaocercria.api.repositorio.RepositorioEstoqueMedicamento;

@RestController
@RequestMapping("/controle-medicamento/agenda")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleAgendaMedicamento {

        @Autowired
        private RepositorioEstoqueMedicamento estoqueRepo;

        @Autowired
        private RepositorioAgendaMedicamento agendaRepo;

        @Autowired
        private RepositorioAgendaMedicamento acao;

        @GetMapping("/listagem")
        public Iterable<AgendaMedicamento> selecionar() {
                return acao.findAll();
        }

        @GetMapping("/{id}")
        public AgendaMedicamento buscarPorId(@PathVariable Long id) {
                return acao.findById(id).orElse(null);
        }

        @GetMapping("/acolhido/{id}")
        public List<AgendaMedicamento> listarPorAcolhido(@PathVariable Integer id) {
                return acao.findByAcolhidoId(id);
        }

        @PutMapping("/tomou/{id}")
        public AgendaMedicamento confirmarDose(@PathVariable Long id) {

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
        public AgendaMedicamento naoTomou(@PathVariable Long id,@RequestBody String motivo) {
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

                EstoqueMedicamento estoque = estoqueRepo.findTopByMedicamentoIdOrderByDataEntradaDesc(
                        agenda.getMedicamento().getId()
                );

                if (estoque == null) {
                        throw new RuntimeException(
                        "Medicamento sem estoque"
                        );
                }

                if (estoque.getQuantidade_atual() < agenda.getDose()) {
                        throw new RuntimeException(
                        "Estoque insuficiente"
                        );
                }

                estoque.setQuantidade_atual(estoque.getQuantidade_atual() - agenda.getDose());

                estoqueRepo.save(estoque);

                agenda.setEstoqueMedicamento(estoque);

                agenda.setStatus("DADO");

                agenda.setDataBaixa(LocalDate.now().toString());

                return agendaRepo.save(agenda);
        }
        
}