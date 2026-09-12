package br.com.bemdoar.entity;

import br.com.bemdoar.enums.ResultadoParticipacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * RF20, RF21 - resultado real de uma candidatura APROVADA (RN16).
 * A relacao com Candidatura e 1:0..1 - por isso a coluna e unique.
 */
@Entity
@Table(name = "participacao_voluntario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacaoVoluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidatura_id", nullable = false, unique = true)
    private Candidatura candidatura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ResultadoParticipacao resultado;

    @Column(length = 500)
    private String observacao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;
}
