package br.com.gestaocercria.api.entidade;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Acolhido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private String escola;
    private String localFamiliar;
    private String numeroProcesso;
    private String vara;
    private Date dataEntrada;
    private String corPele;
    private Date dataSaida;
    private String deficiencia;
    private String alergias;
    private String ppcaam;
    private String tamanhoCamiseta;
    private String tamanhoBermudaCalca;
    private Integer tamanhoCalcado;
    private String tamanhoRoupaIntima;
    List<Long> medicamentos;
    private Boolean excluido = false;
    
    public boolean isAtivo() {
        return this.dataSaida == null;
    }
}
