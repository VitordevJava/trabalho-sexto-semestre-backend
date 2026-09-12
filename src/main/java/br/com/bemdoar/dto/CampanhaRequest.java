package br.com.bemdoar.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record CampanhaRequest(@NotBlank String titulo,@NotBlank String descricao,@NotBlank String objetivo,String imagemUrl,
 @NotNull LocalDate dataInicio,@NotNull LocalDate dataFim,@Positive Integer metaMinima) {}
