package br.com.gestaocercria.api.entidade;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
public class EntradaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double quantidade;
    private String dataEntrada;
    private String dataValidade;
    private String origem;
    private String observacao;

    @ManyToOne
    private Produto produto;

    @ManyToOne
    private Funcionario funcionario;
}
