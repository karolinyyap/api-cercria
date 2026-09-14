package br.com.gestaocercria.api.entidade;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String telefone;
    private Date dataNascimento;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String rg;

    private String orgaoEmissor;
    private String uf;
    private String cargo;
    private String escolaridade;
    private Integer cargaHoraria;
    private String sexo;
    private Date dataAdmissao;
    private Date dataSaida;
    private String senha;
    private Boolean excluido = false;

    public boolean isAtivo() {
        return this.dataSaida == null;
    }
}
