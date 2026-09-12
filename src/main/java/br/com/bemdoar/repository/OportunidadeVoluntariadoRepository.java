package br.com.bemdoar.repository;

import br.com.bemdoar.entity.OportunidadeVoluntariado;
import br.com.bemdoar.enums.SituacaoOportunidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OportunidadeVoluntariadoRepository extends JpaRepository<OportunidadeVoluntariado, Long> {

    List<OportunidadeVoluntariado> findBySituacaoOrderByDataAtividadeAsc(SituacaoOportunidade situacao);

    List<OportunidadeVoluntariado> findAllByOrderByDataAtividadeDesc();

    long countBySituacao(SituacaoOportunidade situacao);
}
