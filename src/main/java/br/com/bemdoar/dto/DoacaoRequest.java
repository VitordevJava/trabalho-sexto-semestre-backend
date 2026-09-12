package br.com.bemdoar.dto;
import br.com.bemdoar.enums.FormaEntrega;
import jakarta.validation.constraints.*;
public record DoacaoRequest(Long necessidadeId, Long campanhaId, @NotBlank String item,
 @Positive Integer quantidade, String observacao, @NotNull FormaEntrega formaEntrega) {}
