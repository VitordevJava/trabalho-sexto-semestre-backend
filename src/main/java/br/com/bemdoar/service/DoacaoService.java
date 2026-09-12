package br.com.bemdoar.service;

import br.com.bemdoar.dto.CancelamentoRequest;
import br.com.bemdoar.dto.DoacaoRequest;
import br.com.bemdoar.dto.DoacaoResponse;
import br.com.bemdoar.entity.Campanha;
import br.com.bemdoar.entity.Doacao;
import br.com.bemdoar.entity.Necessidade;
import br.com.bemdoar.enums.SituacaoCampanha;
import br.com.bemdoar.enums.SituacaoDoacao;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.CampanhaRepository;
import br.com.bemdoar.repository.DoacaoRepository;
import br.com.bemdoar.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CampanhaRepository campanhaRepository;
    private final NecessidadeService necessidadeService;
    private final NotificacaoService notificacaoService;

    public DoacaoService(DoacaoRepository doacaoRepository,
                         UsuarioRepository usuarioRepository,
                         CampanhaRepository campanhaRepository,
                         NecessidadeService necessidadeService,
                         NotificacaoService notificacaoService) {
        this.doacaoRepository = doacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.campanhaRepository = campanhaRepository;
        this.necessidadeService = necessidadeService;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public DoacaoResponse criar(Long usuarioId, DoacaoRequest request) {
        if (request.necessidadeId() == null && request.campanhaId() == null) {
            throw new RegraNegocioException("Informe uma necessidade ou campanha para a doacao.");
        }

        Necessidade necessidade = request.necessidadeId() == null
                ? null : necessidadeService.buscarDisponivelOuFalhar(request.necessidadeId());
        Campanha campanha = request.campanhaId() == null
                ? null : buscarCampanhaDisponivel(request.campanhaId());

        if (campanha != null && necessidade != null
                && campanha.getNecessidades().stream().noneMatch(n -> n.getId().equals(necessidade.getId()))) {
            throw new RegraNegocioException("A necessidade nao pertence a esta campanha.");
        }

        Doacao doacao = new Doacao();
        doacao.setUsuario(usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado.")));
        doacao.setNecessidade(necessidade);
        doacao.setCampanha(campanha);
        doacao.setItem(request.item().trim());
        doacao.setQuantidade(request.quantidade());
        doacao.setObservacao(limpar(request.observacao()));
        doacao.setFormaEntrega(request.formaEntrega());
        doacao.setSituacao(SituacaoDoacao.PENDENTE);
        doacao.setDataRegistro(LocalDateTime.now());
        return DoacaoResponse.de(doacaoRepository.save(doacao));
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponse> listarMinhas(Long usuarioId) {
        return doacaoRepository.findByUsuarioIdOrderByDataRegistroDesc(usuarioId)
                .stream().map(DoacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public DoacaoResponse buscar(Long id, Long usuarioId, boolean administrador) {
        Doacao doacao = buscarEntidade(id);
        validarDonoOuAdministrador(doacao, usuarioId, administrador);
        return DoacaoResponse.de(doacao);
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponse> listarTodas(SituacaoDoacao situacao) {
        List<Doacao> doacoes = situacao == null
                ? doacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataRegistro"))
                : doacaoRepository.findBySituacaoOrderByDataRegistroDesc(situacao);
        return doacoes.stream().map(DoacaoResponse::de).toList();
    }

    @Transactional
    public DoacaoResponse confirmar(Long id) {
        Doacao doacao = buscarEntidade(id);
        exigirSituacao(doacao, SituacaoDoacao.PENDENTE);
        doacao.setSituacao(SituacaoDoacao.CONFIRMADA);
        doacao.setDataConfirmacao(LocalDateTime.now());
        doacaoRepository.save(doacao);
        notificacaoService.notificar(doacao.getUsuario().getId(),
                "Sua doacao de " + doacao.getItem() + " foi confirmada.");
        return DoacaoResponse.de(doacao);
    }

    @Transactional
    public DoacaoResponse receber(Long id) {
        Doacao doacao = buscarEntidade(id);
        exigirSituacao(doacao, SituacaoDoacao.CONFIRMADA);
        doacao.setSituacao(SituacaoDoacao.RECEBIDA);
        doacao.setDataRecebimento(LocalDateTime.now());
        doacaoRepository.save(doacao);
        if (doacao.getNecessidade() != null) {
            necessidadeService.recalcularProgresso(doacao.getNecessidade().getId());
        }
        notificacaoService.notificar(doacao.getUsuario().getId(),
                "Sua doacao de " + doacao.getItem() + " foi recebida.");
        return DoacaoResponse.de(doacao);
    }

    @Transactional
    public DoacaoResponse cancelar(Long id, Long usuarioId, boolean administrador,
                                   CancelamentoRequest request) {
        Doacao doacao = buscarEntidade(id);
        validarDonoOuAdministrador(doacao, usuarioId, administrador);
        if (doacao.getSituacao() != SituacaoDoacao.PENDENTE
                && doacao.getSituacao() != SituacaoDoacao.CONFIRMADA) {
            transicaoInvalida();
        }

        String motivo = request == null ? null : limpar(request.motivo());
        if (administrador && motivo == null) {
            throw new RegraNegocioException("Informe o motivo do cancelamento.");
        }
        doacao.setMotivoCancelamento(motivo);
        doacao.setSituacao(SituacaoDoacao.CANCELADA);
        return DoacaoResponse.de(doacaoRepository.save(doacao));
    }

    private Campanha buscarCampanhaDisponivel(Long id) {
        Campanha campanha = campanhaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campanha nao encontrada."));
        if (campanha.getSituacao() != SituacaoCampanha.ATIVA) {
            throw new RegraNegocioException("Este destino nao esta recebendo novas contribuicoes.");
        }
        return campanha;
    }

    private Doacao buscarEntidade(Long id) {
        return doacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doacao nao encontrada."));
    }

    private void validarDonoOuAdministrador(Doacao doacao, Long usuarioId, boolean administrador) {
        if (!administrador && !doacao.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("Sem permissao");
        }
    }

    private void exigirSituacao(Doacao doacao, SituacaoDoacao esperada) {
        if (doacao.getSituacao() != esperada) {
            transicaoInvalida();
        }
    }

    private void transicaoInvalida() {
        throw new RegraNegocioException("Esta transicao nao e permitida.");
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
