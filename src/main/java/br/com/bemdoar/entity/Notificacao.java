package br.com.bemdoar.entity;

import br.com.bemdoar.enums.EstadoNotificacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * RF27 - mensagem interna destinada a UM usuario.
 * RN18: so o destinatario pode ler.
 * Eventos que geram notificacao: doacao confirmada/recebida,
 * candidatura aprovada/recusada e campanha ativada.
 */
@Entity
@Table(name = "notificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoNotificacao estado;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
}
