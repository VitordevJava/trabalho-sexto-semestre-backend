package br.com.bemdoar.dto;

import br.com.bemdoar.entity.OportunidadeVoluntariado;

import java.time.LocalDate;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * RN15 - vagasOcupadas vem do COUNT de candidaturas APROVADA (nunca de um
 * contador manual na entidade), por isso o metodo 'de' recebe o valor
 * ja calculado pelo Service em vez de le-lo direto da entidade.
 */
public record OportunidadeResponse(
        Long id,
        String titulo,
        String descricao,
        String atividade,
        Integer vagas,
        long vagasOcupadas,
        long vagasLivres,
        LocalDate dataAtividade,
        String horario,
        String local,
        Integer idadeMinima,
        String requisitos,
        String situacao
) {
    public static OportunidadeResponse de(OportunidadeVoluntariado o, long vagasOcupadas) {
        long vagasLivres = Math.max(0, o.getVagas() - vagasOcupadas);
        return new OportunidadeResponse(
                o.getId(),
                o.getTitulo(),
                o.getDescricao(),
                o.getAtividade(),
                o.getVagas(),
                vagasOcupadas,
                vagasLivres,
                o.getDataAtividade(),
                o.getHorario(),
                o.getLocal(),
                o.getIdadeMinima(),
                o.getRequisitos(),
                o.getSituacao().name()
        );
    }
}
