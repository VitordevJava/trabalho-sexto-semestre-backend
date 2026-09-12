package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Notificacao;
import br.com.bemdoar.enums.EstadoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    long countByUsuarioIdAndEstado(Long usuarioId, EstadoNotificacao estado);
}
