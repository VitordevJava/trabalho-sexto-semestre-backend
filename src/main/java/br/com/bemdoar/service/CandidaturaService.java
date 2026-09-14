package br.com.bemdoar.service;

import br.com.bemdoar.dto.CandidaturaResponse;
import br.com.bemdoar.dto.ParticipacaoRequest;
import br.com.bemdoar.entity.Candidatura;
import br.com.bemdoar.entity.OportunidadeVoluntariado;
import br.com.bemdoar.entity.ParticipacaoVoluntario;
import br.com.bemdoar.entity.Usuario;
import br.com.bemdoar.enums.PerfilUsuario;
import br.com.bemdoar.enums.SituacaoCandidatura;
import br.com.bemdoar.enums.SituacaoOportunidade;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.CandidaturaRepository;
import br.com.bemdoar.repository.OportunidadeVoluntariadoRepository;
import br.com.bemdoar.repository.ParticipacaoVoluntarioRepository;
import br.com.bemdoar.repository.UsuarioRepository;
import br.com.bemdoar.security.UsuarioAutenticado;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * FRENTE 3 - VOLUNTARIADO.
 *
 * Todas as regras de negocio do fluxo de candidatura/aprovacao/participacao
 * ficam aqui. O Controller so chama estes metodos.
 *
 * RN15 - "vagas ocupadas" nunca e um contador manual: e sempre o COUNT
 * de candidaturas APROVADA, calculado na hora (ver CandidaturaRepository).
 */
@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final OportunidadeVoluntariadoRepository oportunidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipacaoVoluntarioRepository participacaoRepository;
    private final NotificacaoService notificacaoService;

    public CandidaturaService(CandidaturaRepository candidaturaRepository,
                              OportunidadeVoluntariadoRepository oportunidadeRepository,
                              UsuarioRepository usuarioRepository,
                              ParticipacaoVoluntarioRepository participacaoRepository,
                              NotificacaoService notificacaoService) {
        this.candidaturaRepository = candidaturaRepository;
        this.oportunidadeRepository = oportunidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.participacaoRepository = participacaoRepository;
        this.notificacaoService = notificacaoService;
    }

    // ---------------------------------------------------------------
    // CANDIDATAR-SE
    // RN13 - nao pode ter candidatura ATIVA (PENDENTE ou APROVADA)
    //        duplicada para a mesma oportunidade.
    // RN14 - se houver idade minima, valida a idade do usuario.
    // RN26 - se a oportunidade ja estiver lotada (ocupadas == vagas),
    //        novas candidaturas sao bloqueadas (mas a oportunidade
    //        continua ABERTA, nunca e encerrada sozinha).
    // ---------------------------------------------------------------
    @Transactional
    public CandidaturaResponse candidatar(Long usuarioId, Long oportunidadeId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));

        OportunidadeVoluntariado oportunidade = oportunidadeRepository.findById(oportunidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade nao encontrada."));

        if (oportunidade.getSituacao() != SituacaoOportunidade.ABERTA) {
            throw new RegraNegocioException("Esta oportunidade nao esta aberta para candidaturas.");
        }

        if (candidaturaRepository.existsByUsuarioIdAndOportunidadeIdAndSituacaoIn(
                usuarioId, oportunidadeId,
                List.of(SituacaoCandidatura.PENDENTE, SituacaoCandidatura.APROVADA))) {
            throw new RegraNegocioException("Voce ja possui candidatura ativa para esta oportunidade.");
        }

        long ocupadas = candidaturaRepository.countByOportunidadeIdAndSituacao(
                oportunidadeId, SituacaoCandidatura.APROVADA);
        if (ocupadas >= oportunidade.getVagas()) {
            throw new RegraNegocioException("Nao ha vagas disponiveis nesta oportunidade.");
        }

        if (oportunidade.getIdadeMinima() != null) {
            if (usuario.getDataNascimento() == null) {
                throw new RegraNegocioException(
                        "Atualize sua data de nascimento no perfil para se candidatar a esta oportunidade.");
            }
            int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
            if (idade < oportunidade.getIdadeMinima()) {
                throw new RegraNegocioException(
                        "Idade minima exigida para esta oportunidade: " + oportunidade.getIdadeMinima() + " anos.");
            }
        }

        Candidatura candidatura = new Candidatura();
        candidatura.setUsuario(usuario);
        candidatura.setOportunidade(oportunidade);
        candidatura.setSituacao(SituacaoCandidatura.PENDENTE);
        candidatura.setDataCandidatura(LocalDateTime.now());

        return paraResponse(candidaturaRepository.save(candidatura));
    }

    // ---------------------------------------------------------------
    // MINHAS CANDIDATURAS (RN18 - so as do proprio usuario logado)
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<CandidaturaResponse> listarMinhas(Long usuarioId) {
        return candidaturaRepository.findByUsuarioIdOrderByDataCandidaturaDesc(usuarioId)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    // ---------------------------------------------------------------
    // CANDIDATOS DE UMA OPORTUNIDADE (RN18 - somente administrador,
    // ja garantido pelo @PreAuthorize do Controller)
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<CandidaturaResponse> listarPorOportunidade(Long oportunidadeId) {
        if (!oportunidadeRepository.existsById(oportunidadeId)) {
            throw new RecursoNaoEncontradoException("Oportunidade nao encontrada.");
        }
        return candidaturaRepository.findByOportunidadeIdOrderByDataCandidaturaDesc(oportunidadeId)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    // ---------------------------------------------------------------
    // APROVAR: PENDENTE -> APROVADA
    // RN15 - a regra mais importante da frente: nunca aprovar mais
    // candidatos do que o numero de vagas.
    // RF27 - notifica o usuario.
    // ---------------------------------------------------------------
    @Transactional
    public CandidaturaResponse aprovar(Long candidaturaId) {
        Candidatura candidatura = buscarEntidade(candidaturaId);

        if (candidatura.getSituacao() != SituacaoCandidatura.PENDENTE) {
            throw new RegraNegocioException("Somente candidaturas pendentes podem ser aprovadas.");
        }

        Long oportunidadeId = candidatura.getOportunidade().getId();
        long ocupadas = candidaturaRepository.countByOportunidadeIdAndSituacao(
                oportunidadeId, SituacaoCandidatura.APROVADA);

        if (ocupadas >= candidatura.getOportunidade().getVagas()) {
            throw new RegraNegocioException("Nao ha vagas disponiveis.");
        }

        candidatura.setSituacao(SituacaoCandidatura.APROVADA);
        candidatura.setMotivoRecusa(null);
        Candidatura salva = candidaturaRepository.save(candidatura);

        notificacaoService.notificar(
                salva.getUsuario().getId(),
                "Sua candidatura para \"" + salva.getOportunidade().getTitulo() + "\" foi aprovada.");

        return paraResponse(salva);
    }

    // ---------------------------------------------------------------
    // RECUSAR: PENDENTE -> RECUSADA
    // RF27 - notifica o usuario.
    // ---------------------------------------------------------------
    @Transactional
    public CandidaturaResponse recusar(Long candidaturaId, String motivo) {
        Candidatura candidatura = buscarEntidade(candidaturaId);

        if (candidatura.getSituacao() != SituacaoCandidatura.PENDENTE) {
            throw new RegraNegocioException("Somente candidaturas pendentes podem ser recusadas.");
        }

        candidatura.setSituacao(SituacaoCandidatura.RECUSADA);
        candidatura.setMotivoRecusa(motivo);
        Candidatura salva = candidaturaRepository.save(candidatura);

        String mensagem = "Sua candidatura para \"" + salva.getOportunidade().getTitulo() + "\" foi recusada.";
        if (motivo != null && !motivo.isBlank()) {
            mensagem += " Motivo: " + motivo;
        }
        notificacaoService.notificar(salva.getUsuario().getId(), mensagem);

        return paraResponse(salva);
    }

    // ---------------------------------------------------------------
    // CANCELAR (dono da candidatura OU administrador)
    // Se a candidatura estava APROVADA, a vaga fica livre automaticamente
    // (RN15 - o calculo e sempre pelo COUNT, nao ha contador para "zerar").
    // ---------------------------------------------------------------
    @Transactional
    public CandidaturaResponse cancelar(Long candidaturaId, UsuarioAutenticado logado) {
        Candidatura candidatura = buscarEntidade(candidaturaId);

        boolean dono = candidatura.getUsuario().getId().equals(logado.getId());
        boolean admin = logado.getUsuario().getPerfil() == PerfilUsuario.ADMINISTRADOR;
        if (!dono && !admin) {
            throw new AccessDeniedException("Voce nao possui permissao para cancelar esta candidatura.");
        }

        if (candidatura.getSituacao() != SituacaoCandidatura.PENDENTE
                && candidatura.getSituacao() != SituacaoCandidatura.APROVADA) {
            throw new RegraNegocioException("Esta candidatura nao pode ser cancelada.");
        }

        candidatura.setSituacao(SituacaoCandidatura.CANCELADA);

        return paraResponse(candidaturaRepository.save(candidatura));
    }

    // ---------------------------------------------------------------
    // REGISTRAR PARTICIPACAO
    // RN16 - so candidatura APROVADA pode receber participacao, e so
    // uma vez (relacao 1:0..1 com a Candidatura).
    // ---------------------------------------------------------------
    @Transactional
    public CandidaturaResponse registrarParticipacao(Long candidaturaId, ParticipacaoRequest request) {
        Candidatura candidatura = buscarEntidade(candidaturaId);

        if (candidatura.getSituacao() != SituacaoCandidatura.APROVADA) {
            throw new RegraNegocioException(
                    "Somente voluntarios aprovados podem receber registro de participacao");
        }

        if (participacaoRepository.existsByCandidaturaId(candidaturaId)) {
            throw new RegraNegocioException("Esta candidatura ja possui um registro de participacao.");
        }

        ParticipacaoVoluntario participacao = new ParticipacaoVoluntario();
        participacao.setCandidatura(candidatura);
        participacao.setResultado(request.resultado());
        participacao.setObservacao(request.observacao());
        participacao.setDataRegistro(LocalDateTime.now());
        participacaoRepository.save(participacao);

        return paraResponse(candidatura);
    }

    // ---------------------------------------------------------------
    // Metodos internos reaproveitados
    // ---------------------------------------------------------------
    private CandidaturaResponse paraResponse(Candidatura candidatura) {
        ParticipacaoVoluntario participacao =
                participacaoRepository.findByCandidaturaId(candidatura.getId()).orElse(null);
        return CandidaturaResponse.de(candidatura, participacao);
    }

    private Candidatura buscarEntidade(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidatura nao encontrada."));
    }
}
