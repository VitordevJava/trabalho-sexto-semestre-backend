package br.com.bemdoar.dto;
import br.com.bemdoar.entity.ParticipacaoVoluntario;
import java.time.LocalDateTime;
public record ParticipacaoResponse(Long id,Long candidaturaId,String usuarioNome,String oportunidadeTitulo,String resultado,String observacao,LocalDateTime dataRegistro){
 public static ParticipacaoResponse de(ParticipacaoVoluntario p){return new ParticipacaoResponse(p.getId(),p.getCandidatura().getId(),p.getCandidatura().getUsuario().getNome(),p.getCandidatura().getOportunidade().getTitulo(),p.getResultado().name(),p.getObservacao(),p.getDataRegistro());}
}
