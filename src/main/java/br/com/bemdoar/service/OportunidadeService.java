package br.com.bemdoar.service;

import br.com.bemdoar.dto.OportunidadeRequest;
import br.com.bemdoar.dto.OportunidadeResponse;
import br.com.bemdoar.entity.OportunidadeVoluntariado;
import br.com.bemdoar.enums.SituacaoCandidatura;
import br.com.bemdoar.enums.SituacaoOportunidade;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.CandidaturaRepository;
import br.com.bemdoar.repository.OportunidadeVoluntariadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FRENTE 3 - VOLUNTARIADO.
 *
 * Padrao de todo metodo (igual ao molde de Categoria):
 *   1. buscar o que precisa (repository)
 *   2. validar as regras (if -> throw new RegraNegocioException("mensagem"))
 *   3. alterar a entidade
 *   4. salvar
 *   5. devolver DTO
 *
 * RN15: "vagas ocupadas" NUNCA e um campo salvo na entidade. E sempre
 * calculado com CandidaturaRepository.countByOportunidadeIdAndSituacao(
 * id, APROVADA), aqui no Service, para nunca ficar inconsistente.
 */
@Service
public class OportunidadeService {

    private final OportunidadeVoluntariadoRepository oportunidadeRepository;
    private final CandidaturaRepository candidaturaRepository;

    public OportunidadeService(OportunidadeVoluntariadoRepository oportunidadeRepository,
                               CandidaturaRepository candidaturaRepository) {
        this.oportunidadeRepository = oportunidadeRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    // ---------------------------------------------------------------
    // LISTAR (todas as situacoes - PLANEJADA, ABERTA, ENCERRADA, CANCELADA)
    // GET /api/oportunidades e PUBLICO e serve tanto a tela publica
    // (que filtra so ABERTA no front) quanto a tela administrativa
    // (que precisa ver e gerenciar todas as situacoes).
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<OportunidadeResponse> listarTodas() {
        return oportunidadeRepository.findAllByOrderByDataAtividadeDesc()
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    // ---------------------------------------------------------------
    // BUSCAR UMA (com vagas totais/ocupadas/livres)
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public OportunidadeResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    // ---------------------------------------------------------------
    // CRIAR
    // Toda oportunidade nasce PLANEJADA. A abertura e sempre manual,
    // atraves do endpoint /abrir - nunca automatica.
    // ---------------------------------------------------------------
    @Transactional
    public OportunidadeResponse criar(OportunidadeRequest request) {
        OportunidadeVoluntariado oportunidade = new OportunidadeVoluntariado();
        aplicarCampos(oportunidade, request);
        oportunidade.setSituacao(SituacaoOportunidade.PLANEJADA);

        return paraResponse(oportunidadeRepository.save(oportunidade));
    }

    // ---------------------------------------------------------------
    // ATUALIZAR
    // RN15 - nao pode reduzir as vagas abaixo do numero de aprovados.
    // A situacao NAO muda aqui: isso e trabalho de /abrir e /encerrar.
    // ---------------------------------------------------------------
    @Transactional
    public OportunidadeResponse atualizar(Long id, OportunidadeRequest request) {
        OportunidadeVoluntariado oportunidade = buscarEntidade(id);

        long aprovados = candidaturaRepository.countByOportunidadeIdAndSituacao(
                id, SituacaoCandidatura.APROVADA);

        if (request.vagas() < aprovados) {
            throw new RegraNegocioException(
                    "Nao e possivel reduzir as vagas abaixo do numero de aprovados.");
        }

        aplicarCampos(oportunidade, request);

        return paraResponse(oportunidadeRepository.save(oportunidade));
    }

    // ---------------------------------------------------------------
    // ABRIR: PLANEJADA -> ABERTA
    // ---------------------------------------------------------------
    @Transactional
    public OportunidadeResponse abrir(Long id) {
        OportunidadeVoluntariado oportunidade = buscarEntidade(id);

        if (oportunidade.getSituacao() != SituacaoOportunidade.PLANEJADA) {
            throw new RegraNegocioException("Somente oportunidades planejadas podem ser abertas.");
        }

        oportunidade.setSituacao(SituacaoOportunidade.ABERTA);

        return paraResponse(oportunidadeRepository.save(oportunidade));
    }

    // ---------------------------------------------------------------
    // ENCERRAR
    // RN26 - lotar (vagas ocupadas == vagas totais) NAO encerra sozinho.
    // O encerramento e sempre uma decisao manual do administrador.
    // ---------------------------------------------------------------
    @Transactional
    public OportunidadeResponse encerrar(Long id) {
        OportunidadeVoluntariado oportunidade = buscarEntidade(id);

        if (oportunidade.getSituacao() == SituacaoOportunidade.ENCERRADA
                || oportunidade.getSituacao() == SituacaoOportunidade.CANCELADA) {
            throw new RegraNegocioException("Esta oportunidade ja esta encerrada.");
        }

        oportunidade.setSituacao(SituacaoOportunidade.ENCERRADA);

        return paraResponse(oportunidadeRepository.save(oportunidade));
    }

    // ---------------------------------------------------------------
    // Metodos internos reaproveitados
    // ---------------------------------------------------------------
    private void aplicarCampos(OportunidadeVoluntariado oportunidade, OportunidadeRequest request) {
        oportunidade.setTitulo(request.titulo());
        oportunidade.setDescricao(request.descricao());
        oportunidade.setAtividade(request.atividade());
        oportunidade.setVagas(request.vagas());
        oportunidade.setDataAtividade(request.dataAtividade());
        oportunidade.setHorario(request.horario());
        oportunidade.setLocal(request.local());
        oportunidade.setIdadeMinima(request.idadeMinima());
        oportunidade.setRequisitos(request.requisitos());
    }

    private OportunidadeResponse paraResponse(OportunidadeVoluntariado oportunidade) {
        long vagasOcupadas = candidaturaRepository.countByOportunidadeIdAndSituacao(
                oportunidade.getId(), SituacaoCandidatura.APROVADA);
        return OportunidadeResponse.de(oportunidade, vagasOcupadas);
    }

    /** Devolve a ENTIDADE (nao o DTO) e ja trata o "nao existe". */
    private OportunidadeVoluntariado buscarEntidade(Long id) {
        return oportunidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oportunidade nao encontrada."));
    }
}
