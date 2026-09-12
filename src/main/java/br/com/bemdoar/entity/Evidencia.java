package br.com.bemdoar.entity;

import br.com.bemdoar.enums.TipoEvidencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * RF24 - imagem ou documento do resultado de uma acao social.
 * Maximo 5 por acao, 5 MB cada, apenas JPEG / PNG / PDF.
 * O arquivo fica na pasta ./uploads e aqui guardamos so o caminho.
 * O campo 'publica' controla se aparece na Transparencia (RF25).
 */
@Entity
@Table(name = "evidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "acao_id", nullable = false)
    private AcaoSocial acao;

    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoEvidencia tipo;

    @Column(nullable = false, length = 400)
    private String caminho;

    @Column(nullable = false)
    private boolean publica;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;
}
