package br.com.bemdoar.entity;

import br.com.bemdoar.enums.SituacaoCandidatura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * RF18, RF19 - manifestacao de interesse de um usuario em uma oportunidade.
 * RN13: um usuario nao pode ter duas candidaturas ATIVAS (PENDENTE ou
 *       APROVADA) para a mesma oportunidade.
 * RN15: so APROVADA ocupa vaga.
 */
@Entity
@Table(name = "candidatura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "oportunidade_id", nullable = false)
    private OportunidadeVoluntariado oportunidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SituacaoCandidatura situacao;

    @Column(name = "data_candidatura", nullable = false)
    private LocalDateTime dataCandidatura;

    @Column(name = "motivo_recusa", length = 500)
    private String motivoRecusa;
}
