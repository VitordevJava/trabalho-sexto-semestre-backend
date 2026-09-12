package br.com.bemdoar.dto;
import br.com.bemdoar.entity.Evidencia; import java.time.LocalDateTime;
public record EvidenciaResponse(Long id,String nomeArquivo,String tipo,String caminho,boolean publica,LocalDateTime dataUpload){public static EvidenciaResponse de(Evidencia e){return new EvidenciaResponse(e.getId(),e.getNomeArquivo(),e.getTipo().name(),e.getCaminho(),e.isPublica(),e.getDataUpload());}}
