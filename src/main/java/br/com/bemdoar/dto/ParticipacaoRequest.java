package br.com.bemdoar.dto;
import br.com.bemdoar.enums.ResultadoParticipacao; import jakarta.validation.constraints.NotNull;
public record ParticipacaoRequest(@NotNull ResultadoParticipacao resultado,String observacao) {}
