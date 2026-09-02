package br.com.gestaocercria.api.entidade;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String email;
    private String cpf;
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
    private Boolean senhaTemporaria = false;

    public boolean isAtivo() {
        return this.dataSaida == null;
    }
}
