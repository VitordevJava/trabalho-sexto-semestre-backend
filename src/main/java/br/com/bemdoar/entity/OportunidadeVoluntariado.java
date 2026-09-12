package br.com.bemdoar.entity;

import br.com.bemdoar.enums.SituacaoOportunidade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * RF17, RF18 - atividade de voluntariado com vagas.
 * RN15: o numero de candidaturas APROVADA nunca passa de 'vagas'.
 * RN26: comeca PLANEJADA; a abertura e manual. Lotar nao encerra sozinho.
 */
@Entity
@Table(name = "oportunidade_voluntariado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OportunidadeVoluntariado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 500)
    private String atividade;

    @Column(nullable = false)
    private Integer vagas;

    @Column(name = "data_atividade", nullable = false)
    private LocalDate dataAtividade;

    @Column(nullable = false, length = 50)
    private String horario;

    @Column(name = "local_texto", nullable = false, length = 300)
    private String local;

    /** RN14 - opcional. Quando preenchida, e validada pela data de nascimento. */
    @Column(name = "idade_minima")
    private Integer idadeMinima;

    @Column(length = 1000)
    private String requisitos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SituacaoOportunidade situacao;
}
