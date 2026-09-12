package br.com.bemdoar.dto;
import br.com.bemdoar.entity.OportunidadeVoluntariado; import java.time.LocalDate;
public record OportunidadeResponse(Long id,String titulo,String descricao,String atividade,Integer vagas,long ocupadas,long livres,
 LocalDate dataAtividade,String horario,String local,Integer idadeMinima,String requisitos,String situacao){
 public static OportunidadeResponse de(OportunidadeVoluntariado o,long ocupadas){return new OportunidadeResponse(o.getId(),o.getTitulo(),o.getDescricao(),o.getAtividade(),o.getVagas(),ocupadas,Math.max(0,o.getVagas()-ocupadas),o.getDataAtividade(),o.getHorario(),o.getLocal(),o.getIdadeMinima(),o.getRequisitos(),o.getSituacao().name());}}
