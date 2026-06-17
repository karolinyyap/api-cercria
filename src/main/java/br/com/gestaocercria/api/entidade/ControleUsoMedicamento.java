package br.com.gestaocercria.api.entidade;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "controle_uso_medicamento")
@Getter
@Setter
public class ControleUsoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double dose;
    private Integer intervalo;
    private String iniciandoEm;
    private Integer vezesAoDia;
    private String horarioFixo;
    private String diasSemana;
    private String dataInicio;
    private String dataFim;
    private Boolean usoContinuo;
    private String observacao;
    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @ManyToOne
    @JoinColumn(name = "acolhido_id")
    private Acolhido acolhido;

    @ManyToOne
    @JoinColumn(name = "funcionario_cadastro_id")
    private Funcionario funcionarioCadastro;
}
