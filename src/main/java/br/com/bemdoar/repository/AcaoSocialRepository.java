package br.com.bemdoar.repository;

import br.com.bemdoar.entity.AcaoSocial;
import br.com.bemdoar.enums.SituacaoAcaoSocial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AcaoSocialRepository extends JpaRepository<AcaoSocial, Long> {

    List<AcaoSocial> findBySituacaoOrderByDataInicioDesc(SituacaoAcaoSocial situacao);

    List<AcaoSocial> findAllByOrderByDataInicioDesc();

    /** RF28 - "ultimas 5 acoes sociais" do dashboard. */
    List<AcaoSocial> findAllByOrderByDataInicioDesc(Pageable pageable);

    boolean existsByNecessidadesId(Long necessidadeId);

    /** RF25 - total de beneficiados reais somado por acao. */
    @Query("SELECT COALESCE(SUM(a.beneficiadosReais), 0) FROM AcaoSocial a WHERE a.situacao = 'REALIZADA'")
    Long somarBeneficiadosReais();
}
