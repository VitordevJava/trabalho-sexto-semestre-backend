package br.com.bemdoar.repository;

import br.com.bemdoar.entity.ParticipacaoVoluntario;
import br.com.bemdoar.enums.ResultadoParticipacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipacaoVoluntarioRepository extends JpaRepository<ParticipacaoVoluntario, Long> {

    Optional<ParticipacaoVoluntario> findByCandidaturaId(Long candidaturaId);

    boolean existsByCandidaturaId(Long candidaturaId);

    List<ParticipacaoVoluntario> findByCandidaturaUsuarioIdOrderByDataRegistroDesc(Long usuarioId);

    List<ParticipacaoVoluntario> findAllByOrderByDataRegistroDesc();

    long countByResultado(ResultadoParticipacao resultado);

    /** RF25 - voluntarios DISTINTOS, diferente do total de participacoes. */
    @Query("SELECT COUNT(DISTINCT p.candidatura.usuario.id) FROM ParticipacaoVoluntario p WHERE p.resultado = 'PRESENTE'")
    long contarVoluntariosDistintos();
}
