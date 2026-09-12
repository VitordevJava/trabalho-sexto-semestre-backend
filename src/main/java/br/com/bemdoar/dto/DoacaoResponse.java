package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Doacao;
import java.time.LocalDateTime;
public record DoacaoResponse(Long id,Long usuarioId,String usuarioNome,Long necessidadeId,String necessidadeTitulo,
 Long campanhaId,String campanhaTitulo,String item,Integer quantidade,String observacao,String formaEntrega,String situacao,
 LocalDateTime dataRegistro,String motivoCancelamento){
 public static DoacaoResponse de(Doacao d){return new DoacaoResponse(d.getId(),d.getUsuario().getId(),d.getUsuario().getNome(),
 d.getNecessidade()==null?null:d.getNecessidade().getId(),d.getNecessidade()==null?null:d.getNecessidade().getTitulo(),
 d.getCampanha()==null?null:d.getCampanha().getId(),d.getCampanha()==null?null:d.getCampanha().getTitulo(),d.getItem(),d.getQuantidade(),
 d.getObservacao(),d.getFormaEntrega().name(),d.getSituacao().name(),d.getDataRegistro(),d.getMotivoCancelamento());}
}
