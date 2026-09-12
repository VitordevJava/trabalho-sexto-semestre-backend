package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Candidatura;
import br.com.bemdoar.enums.SituacaoCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    List<Candidatura> findByUsuarioIdOrderByDataCandidaturaDesc(Long usuarioId);

    List<Candidatura> findByOportunidadeIdOrderByDataCandidaturaDesc(Long oportunidadeId);

    /** RN13 - ja existe candidatura ATIVA (PENDENTE ou APROVADA) deste usuario? */
    boolean existsByUsuarioIdAndOportunidadeIdAndSituacaoIn(
            Long usuarioId, Long oportunidadeId, List<SituacaoCandidatura> situacoes);

    /** RN15 - quantas vagas ja estao ocupadas. */
    long countByOportunidadeIdAndSituacao(Long oportunidadeId, SituacaoCandidatura situacao);

    List<Candidatura> findByOportunidadeIdAndSituacao(Long oportunidadeId, SituacaoCandidatura situacao);

    long countBySituacao(SituacaoCandidatura situacao);
}
