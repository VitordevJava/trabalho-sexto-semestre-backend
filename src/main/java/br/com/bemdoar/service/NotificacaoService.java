package br.com.bemdoar.service;

import br.com.bemdoar.entity.Notificacao;
import br.com.bemdoar.entity.Usuario;
import br.com.bemdoar.enums.EstadoNotificacao;
import br.com.bemdoar.repository.NotificacaoRepository;
import br.com.bemdoar.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * RF27 - LADO DA CRIACAO das notificacoes.
 *
 * ====================================================================
 * ISTO E UM CONTRATO ENTRE AS FRENTES.
 *
 * As Frentes 1, 2 e 3 precisam CRIAR notificacoes, mas quem constroi
 * as telas de notificacao e a Frente 4. Para ninguem ficar esperando
 * ninguem, o metodo de criacao ja esta pronto aqui.
 *
 * COMO USAR (exemplo real, Frente 1, ao confirmar uma doacao):
 *
 *   private final NotificacaoService notificacaoService;   // no construtor
 *   ...
 *   notificacaoService.notificar(
 *       doacao.getUsuario().getId(),
 *       "Sua doacao de " + doacao.getItem() + " foi confirmada.");
 *
 * Eventos previstos no documento (RF27):
 *   Frente 1 - doacao confirmada / doacao recebida
 *   Frente 2 - campanha ativada (notificar TODOS os usuarios)
 *   Frente 3 - candidatura aprovada / candidatura recusada
 *
 * A Frente 4 acrescenta neste arquivo os metodos de LEITURA
 * (listarMinhas e marcarComoLida). Nao apague o que ja esta aqui.
 * ====================================================================
 */
@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                              UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Cria uma notificacao NAO_LIDA para um usuario. */
    @Transactional
    public void notificar(Long usuarioId, String mensagem) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return; // RN20 - nunca criar registro orfao
        }
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setEstado(EstadoNotificacao.NAO_LIDA);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacaoRepository.save(notificacao);
    }

    /** Usado pela Frente 2 no evento "campanha ativada". */
    @Transactional
    public void notificarTodosUsuarios(String mensagem) {
        usuarioRepository
                .findByPerfilAndAtivoTrue(br.com.bemdoar.enums.PerfilUsuario.USUARIO)
                .forEach(usuario -> notificar(usuario.getId(), mensagem));
    }
}
