package br.com.bemdoar.entity;

import br.com.bemdoar.enums.SituacaoComunicado;
import br.com.bemdoar.enums.TipoComunicado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * RF26 - aviso geral da instituicao.
 * PUBLICO  -> qualquer visitante ve.
 * INTERNO  -> so usuario autenticado ve.
 * Arquivar nao apaga: so tira da listagem principal.
 */
@Entity
@Table(name = "comunicado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 4000)
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoComunicado tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private SituacaoComunicado situacao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;
}
