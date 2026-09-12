package br.com.bemdoar.dto;
import jakarta.validation.constraints.*; import java.time.LocalDate;
public record AcaoSocialRequest(@NotBlank String titulo,@NotBlank String descricao,@NotBlank String objetivo,@NotNull LocalDate dataInicio,
 @NotNull LocalDate dataFim,@NotBlank String local,@NotBlank String publicoAtendido,@Positive Integer estimativaBeneficiados) {}
