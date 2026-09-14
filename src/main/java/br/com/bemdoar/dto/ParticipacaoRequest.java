package br.com.bemdoar.dto;

import br.com.bemdoar.enums.ResultadoParticipacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * RN16 - so pode ser enviado para uma candidatura APROVADA
 * (a validacao de "aprovada ou nao" fica no Service, nunca aqui).
 */
public record ParticipacaoRequest(

        @NotNull(message = "Informe o resultado da participacao.")
        ResultadoParticipacao resultado,

        @Size(max = 500, message = "Observacao muito longa.")
        String observacao
) {
}
