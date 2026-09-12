package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Campanha;
import br.com.bemdoar.enums.SituacaoCampanha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    List<Campanha> findBySituacaoOrderByDataInicioDesc(SituacaoCampanha situacao);

    List<Campanha> findAllByOrderByDataInicioDesc();

    long countBySituacao(SituacaoCampanha situacao);

    /** RF09 - impede excluir necessidade que esta vinculada a alguma campanha. */
    boolean existsByNecessidadesId(Long necessidadeId);
}
