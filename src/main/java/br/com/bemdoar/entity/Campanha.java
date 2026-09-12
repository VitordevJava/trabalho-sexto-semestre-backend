package br.com.bemdoar.entity;

import br.com.bemdoar.enums.SituacaoCampanha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * RF14 a RF16 - mobilizacao com objetivo, periodo e meta minima.
 * RN12: dataFim >= dataInicio. Comeca sempre PLANEJADA.
 * RN23: a meta e um piso, nao um teto. O progresso pode passar de 100%.
 * RN24: a mesma necessidade pode estar em varias campanhas (N:N abaixo).
 */
@Entity
@Table(name = "campanha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 1000)
    private String objetivo;

    @Column(name = "imagem_url", length = 300)
    private String imagemUrl;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "meta_minima", nullable = false)
    private Integer metaMinima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SituacaoCampanha situacao;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    /** RF15 - N:N com Necessidade, representado por tabela de associacao simples. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "campanha_necessidade",
            joinColumns = @JoinColumn(name = "campanha_id"),
            inverseJoinColumns = @JoinColumn(name = "necessidade_id")
    )
    private Set<Necessidade> necessidades = new HashSet<>();
}
