package br.com.bemdoar.dto;

import br.com.bemdoar.entity.Doacao;
import br.com.bemdoar.enums.FormaEntrega;
import br.com.bemdoar.enums.SituacaoDoacao;

import java.time.LocalDateTime;

public record DoacaoResponse(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Long necessidadeId,
        String necessidadeTitulo,
        Long campanhaId,
        String campanhaTitulo,
        String item,
        Integer quantidade,
        String observacao,
        FormaEntrega formaEntrega,
        SituacaoDoacao situacao,
        LocalDateTime dataRegistro,
        LocalDateTime dataConfirmacao,
        LocalDateTime dataRecebimento,
        String motivoCancelamento) {

    public static DoacaoResponse de(Doacao doacao) {
        return new DoacaoResponse(
                doacao.getId(),
                doacao.getUsuario().getId(),
                doacao.getUsuario().getNome(),
                doacao.getNecessidade() == null ? null : doacao.getNecessidade().getId(),
                doacao.getNecessidade() == null ? null : doacao.getNecessidade().getTitulo(),
                doacao.getCampanha() == null ? null : doacao.getCampanha().getId(),
                doacao.getCampanha() == null ? null : doacao.getCampanha().getTitulo(),
                doacao.getItem(),
                doacao.getQuantidade(),
                doacao.getObservacao(),
                doacao.getFormaEntrega(),
                doacao.getSituacao(),
                doacao.getDataRegistro(),
                doacao.getDataConfirmacao(),
                doacao.getDataRecebimento(),
                doacao.getMotivoCancelamento());
    }
}
