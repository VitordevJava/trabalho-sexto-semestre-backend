package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Candidatura; import java.time.LocalDateTime;
public record CandidaturaResponse(Long id,Long usuarioId,String usuarioNome,Long oportunidadeId,String oportunidadeTitulo,String situacao,
 LocalDateTime dataCandidatura,String motivoRecusa,String resultado){
 public static CandidaturaResponse de(Candidatura c,String resultado){return new CandidaturaResponse(c.getId(),c.getUsuario().getId(),c.getUsuario().getNome(),c.getOportunidade().getId(),c.getOportunidade().getTitulo(),c.getSituacao().name(),c.getDataCandidatura(),c.getMotivoRecusa(),resultado);}}
