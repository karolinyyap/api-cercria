package br.com.gestaocercria.api.entidade;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class EstoqueMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int quantidade;
    private int quantidade_atual;
    private Date dataValidade;
    private String origem;
    private String responsavel;
    private Date dataEntrada;

    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;
}
