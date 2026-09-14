package br.com.bemdoar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * Dados que o administrador digita para criar/editar uma oportunidade.
 * RN15: vagas usa @Positive - o Spring valida sozinho, sem if manual.
 */
public record OportunidadeRequest(

        @NotBlank(message = "Informe o titulo.")
        @Size(max = 150, message = "Titulo muito longo.")
        String titulo,

        @NotBlank(message = "Informe a descricao.")
        @Size(max = 2000, message = "Descricao muito longa.")
        String descricao,

        @NotBlank(message = "Informe a atividade.")
        @Size(max = 500, message = "Atividade muito longa.")
        String atividade,

        @NotNull(message = "Informe o numero de vagas.")
        @Positive(message = "O numero de vagas deve ser maior que zero.")
        Integer vagas,

        @NotNull(message = "Informe a data da atividade.")
        LocalDate dataAtividade,

        @NotBlank(message = "Informe o horario.")
        @Size(max = 50, message = "Horario muito longo.")
        String horario,

        @NotBlank(message = "Informe o local.")
        @Size(max = 300, message = "Local muito longo.")
        String local,

        /** RN14 - opcional. Quando preenchida, valida a idade do candidato. */
        @Positive(message = "A idade minima deve ser maior que zero.")
        Integer idadeMinima,

        @Size(max = 1000, message = "Requisitos muito longos.")
        String requisitos
) {
}
