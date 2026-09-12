package br.com.bemdoar.service;

import br.com.bemdoar.dto.TransparenciaResponse;
import br.com.bemdoar.enums.SituacaoDoacao;
import br.com.bemdoar.repository.AcaoSocialRepository;
import br.com.bemdoar.repository.CampanhaRepository;
import br.com.bemdoar.repository.DoacaoRepository;
import br.com.bemdoar.repository.ParticipacaoVoluntarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransparenciaService {

    private final DoacaoRepository doacaoRepository;
    private final CampanhaRepository campanhaRepository;
    private final AcaoSocialRepository acaoSocialRepository;
    private final ParticipacaoVoluntarioRepository participacaoRepository;

    public TransparenciaService(DoacaoRepository doacaoRepository,
                                CampanhaRepository campanhaRepository,
                                AcaoSocialRepository acaoSocialRepository,
                                ParticipacaoVoluntarioRepository participacaoRepository) {
        this.doacaoRepository = doacaoRepository;
        this.campanhaRepository = campanhaRepository;
        this.acaoSocialRepository = acaoSocialRepository;
        this.participacaoRepository = participacaoRepository;
    }

    @Transactional(readOnly = true)
    public TransparenciaResponse obter() {
        Long beneficiados = acaoSocialRepository.somarBeneficiadosReais();
        return new TransparenciaResponse(
                doacaoRepository.countBySituacao(SituacaoDoacao.RECEBIDA),
                campanhaRepository.count(),
                acaoSocialRepository.count(),
                beneficiados == null ? 0 : beneficiados,
                participacaoRepository.contarVoluntariosDistintos());
    }
}
