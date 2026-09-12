package br.com.bemdoar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** RF03 / RN04 - trocar senha exige a senha atual. */
public record AlterarSenhaRequest(
        @NotBlank(message = "Informe a senha atual.") String senhaAtual,
        @NotBlank(message = "Informe a nova senha.")
        @Size(min = 8, message = "A senha deve possuir pelo menos 8 caracteres.") String novaSenha,
        @NotBlank(message = "Confirme a nova senha.") String confirmacaoSenha
) {
}
