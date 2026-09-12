package br.com.bemdoar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** RF03 - o usuario altera apenas nome, e-mail e telefone. */
public record AtualizarPerfilRequest(
        @NotBlank(message = "Informe o nome.") String nome,
        @NotBlank(message = "Informe o e-mail.") @Email(message = "E-mail invalido.") String email,
        String telefone
) {
}
