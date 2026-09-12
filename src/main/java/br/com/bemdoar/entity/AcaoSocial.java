package br.com.bemdoar.entity;

import br.com.bemdoar.enums.SituacaoAcaoSocial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * RF22 a RF24 - atividade social planejada ou executada pela instituicao.
 * RN27: comeca PLANEJADA; estados PLANEJADA / REALIZADA / CANCELADA.
 *       Depois de REALIZADA o planejamento fica preservado e os resultados
 *       sao registrados pelos campos descricaoResultado / beneficiadosReais.
 *
 * Os tres N:N do RF23 sao tabelas de associacao simples.
 */
@Entity
@Table(name = "acao_social")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcaoSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 1000)
    private String objetivo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "local_texto", nullable = false, length = 300)
    private String local;

    @Column(name = "publico_atendido", nullable = false, length = 300)
    private String publicoAtendido;

    @Column(name = "estimativa_beneficiados", nullable = false)
    private Integer estimativaBeneficiados;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SituacaoAcaoSocial situacao;

    // ---- RF24: resultado real, separado da estimativa ----
    @Column(name = "descricao_resultado", length = 2000)
    private String descricaoResultado;

    @Column(name = "beneficiados_reais")
    private Integer beneficiadosReais;

    // ---- RF23: vinculos ----
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "acao_campanha",
            joinColumns = @JoinColumn(name = "acao_id"),
            inverseJoinColumns = @JoinColumn(name = "campanha_id"))
    private Set<Campanha> campanhas = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "acao_necessidade",
            joinColumns = @JoinColumn(name = "acao_id"),
            inverseJoinColumns = @JoinColumn(name = "necessidade_id"))
    private Set<Necessidade> necessidades = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "acao_participacao",
            joinColumns = @JoinColumn(name = "acao_id"),
            inverseJoinColumns = @JoinColumn(name = "participacao_id"))
    private Set<ParticipacaoVoluntario> participacoes = new HashSet<>();
}
