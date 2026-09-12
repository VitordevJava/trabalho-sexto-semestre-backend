package br.com.bemdoar.dto;

import br.com.bemdoar.entity.CategoriaNecessidade;

/**
 * ===================== DTO DE SAIDA - MOLDE =====================
 *
 * O que e um DTO de saida: o formato do JSON que a API DEVOLVE.
 *
 * Por que nao devolver a entidade direto? Porque a entidade pode ter
 * campos que nao podem sair (a senha do Usuario, por exemplo - RN18).
 * Usar DTO sempre e o habito seguro.
 *
 * O metodo 'de(...)' converte a entidade em DTO. Sempre estatico,
 * sempre com esse nome, em todas as frentes.
 *
 * PARA COPIAR: troque o nome, os campos e o corpo do 'de'.
 * ================================================================
 */
public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativo
) {
    public static CategoriaResponse de(CategoriaNecessidade categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.isAtivo()
        );
    }
}
