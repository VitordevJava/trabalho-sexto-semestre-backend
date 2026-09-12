package br.com.bemdoar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** RF04 - campos que o administrador edita. */
public record InstituicaoRequest(
        @NotBlank(message = "Informe o nome.") String nome,
        @NotBlank(message = "Informe a descricao.") String descricao,
        @NotBlank(message = "Informe a missao.") String missao,
        @NotBlank(message = "Informe a area de atuacao.") String areaAtuacao,
        @NotBlank(message = "Informe o publico atendido.") String publicoAtendido,
        @NotBlank(message = "Informe o CEP.") String cep,
        @NotBlank(message = "Informe o logradouro.") String logradouro,
        @NotBlank(message = "Informe o numero.") String numero,
        String complemento,
        @NotBlank(message = "Informe o bairro.") String bairro,
        @NotBlank(message = "Informe a cidade.") String cidade,
        @NotBlank(message = "Informe o estado.") @Size(min = 2, max = 2, message = "Use a sigla do estado.") String estado,
        String telefone,
        String whatsapp,
        String email,
        String historia,
        String redesSociais,
        String horarioAtendimento,
        String logoUrl
) {
}
