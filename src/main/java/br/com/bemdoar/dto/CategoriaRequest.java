package br.com.bemdoar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ===================== DTO DE ENTRADA - MOLDE =====================
 *
 * O que e um DTO de entrada: o formato do JSON que o front ENVIA.
 * Repare que NAO tem 'id' - o id vem na URL, nunca no corpo.
 *
 * As anotacoes (@NotBlank, @Size) sao validadas AUTOMATICAMENTE pelo
 * Spring quando o controller usa @Valid. Se falhar, o TratadorDeErros
 * devolve 400 com a mensagem escrita aqui. Voce nao escreve nenhum if.
 *
 * PARA COPIAR: troque 'Categoria' pelo nome da sua entidade e troque
 * os campos. Mantenha o padrao das anotacoes.
 * ==================================================================
 */
public record CategoriaRequest(

        @NotBlank(message = "Informe o nome da categoria.")
        @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres.")
        String nome,

        @Size(max = 500, message = "A descricao deve ter no maximo 500 caracteres.")
        String descricao
) {
}
