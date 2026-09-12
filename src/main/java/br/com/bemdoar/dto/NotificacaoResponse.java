package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Notificacao; import java.time.LocalDateTime;
public record NotificacaoResponse(Long id,String mensagem,String estado,LocalDateTime dataCriacao){public static NotificacaoResponse de(Notificacao n){return new NotificacaoResponse(n.getId(),n.getMensagem(),n.getEstado().name(),n.getDataCriacao());}}
