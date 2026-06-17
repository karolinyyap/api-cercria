package br.com.gestaocercria.api.entidade;

import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private LocalDate data;
    private LocalTime hora;
    private String descricao;

    private List<Integer> responsaveis;
    private List<Integer> acolhidos;
}
