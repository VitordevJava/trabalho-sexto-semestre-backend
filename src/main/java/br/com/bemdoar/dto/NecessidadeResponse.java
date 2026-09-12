package br.com.bemdoar.dto;

import br.com.bemdoar.entity.Necessidade;

import java.time.LocalDateTime;

/**
 * RF07 / RF10 - inclui o progresso ja calculado, para o front nao
 * precisar fazer conta. O percentual pode passar de 100 (RN23).
 */
public record NecessidadeResponse(
        Long id,
        String titulo,
        String descricao,
        Long categoriaId,
        String categoriaNome,
        Integer quantidadeNecessaria,
        Integer quantidadeRecebida,
        int percentual,
        String prioridade,
        String situacao,
        LocalDateTime dataCriacao,
        LocalDateTime dataEncerramento
) {
    public static NecessidadeResponse de(Necessidade n) {
        int percentual = 0;
        if (n.getQuantidadeNecessaria() != null && n.getQuantidadeNecessaria() > 0) {
            percentual = (int) Math.round(
                    (n.getQuantidadeRecebida() * 100.0) / n.getQuantidadeNecessaria());
        }
        return new NecessidadeResponse(
                n.getId(),
                n.getTitulo(),
                n.getDescricao(),
                n.getCategoria().getId(),
                n.getCategoria().getNome(),
                n.getQuantidadeNecessaria(),
                n.getQuantidadeRecebida(),
                percentual,
                n.getPrioridade().name(),
                n.getSituacao().name(),
                n.getDataCriacao(),
                n.getDataEncerramento()
        );
    }
}
