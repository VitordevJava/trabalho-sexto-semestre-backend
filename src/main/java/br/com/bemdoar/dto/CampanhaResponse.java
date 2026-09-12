package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Campanha;
import java.time.LocalDate; import java.util.List;
public record CampanhaResponse(Long id,String titulo,String descricao,String objetivo,String imagemUrl,LocalDate dataInicio,
 LocalDate dataFim,Integer metaMinima,Integer recebido,int percentual,String situacao,String motivoCancelamento,List<NecessidadeResponse> necessidades){
 public static CampanhaResponse de(Campanha c,Integer recebido){int r=recebido==null?0:recebido; int p=c.getMetaMinima()>0?(int)Math.round(r*100.0/c.getMetaMinima()):0;
 return new CampanhaResponse(c.getId(),c.getTitulo(),c.getDescricao(),c.getObjetivo(),c.getImagemUrl(),c.getDataInicio(),c.getDataFim(),
 c.getMetaMinima(),r,p,c.getSituacao().name(),c.getMotivoCancelamento(),c.getNecessidades().stream().map(NecessidadeResponse::de).toList());}
}
