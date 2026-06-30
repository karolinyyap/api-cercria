package br.com.gestaocercria.api.entidade;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
public class SaidaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double quantidade;
    private String dataSaida;
    private String motivo;

    @ManyToOne
    private Produto produto;

    @ManyToOne
    private Funcionario funcionario;
}
