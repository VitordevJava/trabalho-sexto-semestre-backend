package br.com.bemdoar.dto;

import br.com.bemdoar.entity.Candidatura;
import br.com.bemdoar.entity.ParticipacaoVoluntario;

import java.time.LocalDateTime;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * Usado tanto em "Minhas candidaturas" (usuario) quanto na lista de
 * candidatos de uma oportunidade (administrador).
 *
 * 'participacao' vem separada porque a relacao Candidatura -> Participacao
 * e opcional (1:0..1): so existe depois que o administrador registra o
 * resultado (RN16). Por isso o metodo 'de' recebe a participacao (ou null)
 * em vez de le-la direto da candidatura.
 */
public record CandidaturaResponse(
        Long id,
        Long oportunidadeId,
        String oportunidadeTitulo,
        Long usuarioId,
        String usuarioNome,
        String situacao,
        LocalDateTime dataCandidatura,
        String motivoRecusa,
        String resultadoParticipacao,
        String observacaoParticipacao
) {
    public static CandidaturaResponse de(Candidatura candidatura, ParticipacaoVoluntario participacao) {
        return new CandidaturaResponse(
                candidatura.getId(),
                candidatura.getOportunidade().getId(),
                candidatura.getOportunidade().getTitulo(),
                candidatura.getUsuario().getId(),
                candidatura.getUsuario().getNome(),
                candidatura.getSituacao().name(),
                candidatura.getDataCandidatura(),
                candidatura.getMotivoRecusa(),
                participacao != null ? participacao.getResultado().name() : null,
                participacao != null ? participacao.getObservacao() : null
        );
    }
}
