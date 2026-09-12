package br.com.bemdoar.dto;
import jakarta.validation.constraints.*;
public record ResultadoAcaoRequest(@NotBlank String descricaoResultado,@PositiveOrZero Integer beneficiadosReais) {}
