package br.com.bemdoar.service;

import br.com.bemdoar.dto.InstituicaoRequest;
import br.com.bemdoar.dto.InstituicaoResponse;
import br.com.bemdoar.entity.Instituicao;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.InstituicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RF04 - RN01: existe UMA unica instituicao. Nao ha cadastro nem exclusao,
 * apenas leitura e edicao do registro unico criado pelo DataSeeder.
 */
@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
    }

    @Transactional(readOnly = true)
    public InstituicaoResponse consultar() {
        return InstituicaoResponse.de(buscarUnica());
    }

    @Transactional
    public InstituicaoResponse atualizar(InstituicaoRequest request) {

        // RF04 - pelo menos um meio de contato e obrigatorio
        if (vazio(request.telefone()) && vazio(request.whatsapp()) && vazio(request.email())) {
            throw new RegraNegocioException("Informe pelo menos um meio de contato.");
        }

        Instituicao i = buscarUnica();
        i.setNome(request.nome());
        i.setDescricao(request.descricao());
        i.setMissao(request.missao());
        i.setAreaAtuacao(request.areaAtuacao());
        i.setPublicoAtendido(request.publicoAtendido());
        i.setCep(request.cep());
        i.setLogradouro(request.logradouro());
        i.setNumero(request.numero());
        i.setComplemento(request.complemento());
        i.setBairro(request.bairro());
        i.setCidade(request.cidade());
        i.setEstado(request.estado());
        i.setTelefone(request.telefone());
        i.setWhatsapp(request.whatsapp());
        i.setEmail(request.email());
        i.setHistoria(request.historia());
        i.setRedesSociais(request.redesSociais());
        i.setHorarioAtendimento(request.horarioAtendimento());
        i.setLogoUrl(request.logoUrl());

        return InstituicaoResponse.de(instituicaoRepository.save(i));
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private Instituicao buscarUnica() {
        return instituicaoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Instituicao nao encontrada."));
    }
}
