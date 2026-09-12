package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByAcaoId(Long acaoId);

    List<Evidencia> findByAcaoIdAndPublicaTrue(Long acaoId);

    /** RF24 - maximo de 5 evidencias por acao. */
    long countByAcaoId(Long acaoId);
}
