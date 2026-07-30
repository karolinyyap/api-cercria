package br.com.gestaocercria.api.entidade;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Patrimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Long tombamento;
    private String especificacao;
    private Date dtAquisicao;
    private Boolean excluido = false;
}
