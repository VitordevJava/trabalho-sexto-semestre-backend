package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Comunicado; import java.time.LocalDateTime;
public record ComunicadoResponse(Long id,String titulo,String conteudo,String tipo,String situacao,LocalDateTime dataCriacao,LocalDateTime dataPublicacao){
 public static ComunicadoResponse de(Comunicado c){return new ComunicadoResponse(c.getId(),c.getTitulo(),c.getConteudo(),c.getTipo().name(),c.getSituacao().name(),c.getDataCriacao(),c.getDataPublicacao());}}
