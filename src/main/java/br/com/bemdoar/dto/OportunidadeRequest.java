package br.com.bemdoar.dto;
import jakarta.validation.constraints.*; import java.time.LocalDate;
public record OportunidadeRequest(@NotBlank String titulo,@NotBlank String descricao,@NotBlank String atividade,@Positive Integer vagas,
 @NotNull LocalDate dataAtividade,@NotBlank String horario,@NotBlank String local,Integer idadeMinima,String requisitos) {}
