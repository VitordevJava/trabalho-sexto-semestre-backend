package br.com.bemdoar.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** RF01 - dados do cadastro publico. Sempre cria perfil USUARIO (RN02). */
public record CadastroUsuarioRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(max = 120, message = "Nome muito longo.")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "E-mail invalido.")
        String email,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve possuir pelo menos 8 caracteres.")
        String senha,

        @NotBlank(message = "Confirme a senha.")
        String confirmacaoSenha,

        String telefone,

        @NotNull(message = "Informe a data de nascimento.")
        @Past(message = "Data de nascimento invalida.")
        LocalDate dataNascimento,

        @AssertTrue(message = "E necessario aceitar os Termos de Uso e a Politica de Privacidade.")
        boolean aceiteTermos
) {
}
