package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Comunicado;
import br.com.bemdoar.enums.SituacaoComunicado;
import br.com.bemdoar.enums.TipoComunicado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    List<Comunicado> findByTipoAndSituacaoOrderByDataPublicacaoDesc(TipoComunicado tipo, SituacaoComunicado situacao);

    List<Comunicado> findBySituacaoOrderByDataPublicacaoDesc(SituacaoComunicado situacao);

    List<Comunicado> findAllByOrderByDataCriacaoDesc();
}
