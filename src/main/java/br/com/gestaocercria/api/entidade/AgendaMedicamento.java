package br.com.gestaocercria.api.entidade;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.*;

@Table
@Entity
@Getter
@Setter
public class AgendaMedicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String data;
    private String horario;
    private Integer dose;
    private String motivo;
    private String status;
    private String motivoNaoTomou;
    private String dataBaixa;
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "acolhido_id")
    private Acolhido acolhido;

    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @ManyToOne
    @JoinColumn(name = "controle_uso_id")
    private ControleUsoMedicamento controleUso;

    @ManyToOne
    @JoinColumn(name = "estoque_medicamento_id")
    private EstoqueMedicamento estoqueMedicamento;

    @ManyToOne
    @JoinColumn(name = "funcionario_responsavel_id")
    private Funcionario funcionarioResponsavel;
}
