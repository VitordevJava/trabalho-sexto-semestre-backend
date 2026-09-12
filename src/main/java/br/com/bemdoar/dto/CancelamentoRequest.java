package br.com.bemdoar.dto;

import jakarta.validation.constraints.Size;

public record CancelamentoRequest(@Size(max = 500) String motivo) {
}
