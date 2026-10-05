package br.com.gestaocercria.api.entidade;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
public class EstoqueMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double quantidade;

    private Double quantidadeAtual;

    private String dataValidade;
    private String origem;
    private String dataEntrada;

    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario responsavel;
}