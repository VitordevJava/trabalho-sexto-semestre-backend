package br.com.bemdoar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RF04 - dados oficiais da unica organizacao social do sistema (RN01).
 * O endereco fica dentro desta entidade: nao existe entidade Endereco.
 */
@Entity
@Table(name = "instituicao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 1000)
    private String missao;

    @Column(name = "area_atuacao", nullable = false, length = 200)
    private String areaAtuacao;

    @Column(name = "publico_atendido", nullable = false, length = 200)
    private String publicoAtendido;

    // ---- endereco (obrigatorio, exceto complemento) ----
    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 200)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    // ---- contatos: pelo menos um e obrigatorio ----
    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String whatsapp;

    @Column(length = 150)
    private String email;

    // ---- opcionais ----
    @Column(length = 4000)
    private String historia;

    @Column(name = "redes_sociais", length = 500)
    private String redesSociais;

    @Column(name = "horario_atendimento", length = 200)
    private String horarioAtendimento;

    @Column(name = "logo_url", length = 300)
    private String logoUrl;
}
