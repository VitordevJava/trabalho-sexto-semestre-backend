package br.com.bemdoar.service;

import br.com.bemdoar.dto.NecessidadeRequest;
import br.com.bemdoar.dto.NecessidadeResponse;
import br.com.bemdoar.entity.CategoriaNecessidade;
import br.com.bemdoar.entity.Necessidade;
import br.com.bemdoar.enums.Prioridade;
import br.com.bemdoar.enums.SituacaoNecessidade;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.AcaoSocialRepository;
import br.com.bemdoar.repository.CampanhaRepository;
import br.com.bemdoar.repository.DoacaoRepository;
import br.com.bemdoar.repository.NecessidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * RF06 a RF10 - Necessidade.
 *
 * Esta e a entidade P0 do documento (primeira entrega academica).
 * Ela e mais complexa que o molde de Categoria porque tem a regra de
 * PROGRESSO DERIVADO (RN06/RN07/RN10) - o unico ponto do sistema onde
 * um campo do banco nunca pode ser digitado em tela.
 */
@Service
public class NecessidadeService {

    private static final int TAMANHO_PAGINA = 20; // RF07

    private final NecessidadeRepository necessidadeRepository;
    private final CategoriaNecessidadeService categoriaService;
    private final DoacaoRepository doacaoRepository;
    private final CampanhaRepository campanhaRepository;
    private final AcaoSocialRepository acaoSocialRepository;

    public NecessidadeService(NecessidadeRepository necessidadeRepository,
                              CategoriaNecessidadeService categoriaService,
                              DoacaoRepository doacaoRepository,
                              CampanhaRepository campanhaRepository,
                              AcaoSocialRepository acaoSocialRepository) {
        this.necessidadeRepository = necessidadeRepository;
        this.categoriaService = categoriaService;
        this.doacaoRepository = doacaoRepository;
        this.campanhaRepository = campanhaRepository;
        this.acaoSocialRepository = acaoSocialRepository;
    }

    // ---------------------------------------------------------------
    // RF07 - consulta publica com busca e filtros, 20 por pagina,
    //        mais recentes primeiro.
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public Page<NecessidadeResponse> buscar(String termo,
                                            Long categoriaId,
                                            Prioridade prioridade,
                                            SituacaoNecessidade situacao,
                                            int pagina) {

        String termoLimpo = (termo == null || termo.isBlank()) ? null : termo.trim();

        PageRequest paginacao = PageRequest.of(
                Math.max(pagina, 0),
                TAMANHO_PAGINA,
                Sort.by(Sort.Direction.DESC, "dataCriacao"));

        return necessidadeRepository
                .buscar(termoLimpo, categoriaId, prioridade, situacao, paginacao)
                .map(NecessidadeResponse::de);
    }

    @Transactional(readOnly = true)
    public NecessidadeResponse buscarPorId(Long id) {
        return NecessidadeResponse.de(buscarEntidade(id));
    }

    // ---------------------------------------------------------------
    // RF06 - cadastrar
    // RN05: categoria precisa estar ativa.
    // RN07: nasce ABERTA com quantidade recebida = 0.
    // ---------------------------------------------------------------
    @Transactional
    public NecessidadeResponse criar(NecessidadeRequest request) {

        CategoriaNecessidade categoria = categoriaService.buscarAtivaOuFalhar(request.categoriaId());

        Necessidade necessidade = new Necessidade();
        necessidade.setTitulo(request.titulo());
        necessidade.setDescricao(request.descricao());
        necessidade.setCategoria(categoria);
        necessidade.setQuantidadeNecessaria(request.quantidadeNecessaria());
        necessidade.setQuantidadeRecebida(0);
        necessidade.setPrioridade(request.prioridade());
        necessidade.setSituacao(SituacaoNecessidade.ABERTA);
        necessidade.setDataCriacao(LocalDateTime.now());

        return NecessidadeResponse.de(necessidadeRepository.save(necessidade));
    }

    // ---------------------------------------------------------------
    // RF08 - atualizar
    // RN21: encerrada e somente leitura.
    // RN06: nao reduzir a meta abaixo do que ja foi recebido.
    // ---------------------------------------------------------------
    @Transactional
    public NecessidadeResponse atualizar(Long id, NecessidadeRequest request) {

        Necessidade necessidade = buscarEntidade(id);

        if (necessidade.getSituacao() == SituacaoNecessidade.ENCERRADA) {
            throw new RegraNegocioException("Necessidades encerradas nao podem ser editadas.");
        }

        if (request.quantidadeNecessaria() < necessidade.getQuantidadeRecebida()) {
            throw new RegraNegocioException(
                    "A quantidade necessaria nao pode ser menor que a quantidade ja recebida ("
                            + necessidade.getQuantidadeRecebida() + ").");
        }

        // So exige categoria ATIVA quando a categoria realmente mudou (RF08).
        if (!necessidade.getCategoria().getId().equals(request.categoriaId())) {
            necessidade.setCategoria(categoriaService.buscarAtivaOuFalhar(request.categoriaId()));
        }

        necessidade.setTitulo(request.titulo());
        necessidade.setDescricao(request.descricao());
        necessidade.setQuantidadeNecessaria(request.quantidadeNecessaria());
        necessidade.setPrioridade(request.prioridade());

        // A meta mudou, entao a situacao pode ter mudado tambem.
        aplicarSituacaoPorProgresso(necessidade);

        return NecessidadeResponse.de(necessidadeRepository.save(necessidade));
    }

    // ---------------------------------------------------------------
    // RF09 - encerrar (nao apaga nada)
    // ---------------------------------------------------------------
    @Transactional
    public NecessidadeResponse encerrar(Long id) {
        Necessidade necessidade = buscarEntidade(id);

        if (necessidade.getSituacao() == SituacaoNecessidade.ENCERRADA) {
            throw new RegraNegocioException("Esta necessidade ja esta encerrada.");
        }

        necessidade.setSituacao(SituacaoNecessidade.ENCERRADA);
        necessidade.setDataEncerramento(LocalDateTime.now());
        return NecessidadeResponse.de(necessidadeRepository.save(necessidade));
    }

    // ---------------------------------------------------------------
    // RF09 - excluir fisicamente
    // RN17/RN20: so quando NAO existir nenhum vinculo historico.
    // ---------------------------------------------------------------
    @Transactional
    public void excluir(Long id) {
        Necessidade necessidade = buscarEntidade(id);

        if (doacaoRepository.existsByNecessidadeId(id)
                || campanhaRepository.existsByNecessidadesId(id)
                || acaoSocialRepository.existsByNecessidadesId(id)) {
            throw new RegraNegocioException(
                    "Nao e possivel excluir esta necessidade porque existem registros relacionados. Encerre-a.");
        }

        necessidadeRepository.delete(necessidade);
    }

    // ===============================================================
    // RF10 - CORACAO DA REGRA DE PROGRESSO
    //
    // Chame este metodo sempre que uma doacao virar RECEBIDA.
    // Ele NAO soma nem subtrai nada: ele RECALCULA do zero a partir do
    // banco. Assim e impossivel contar duas vezes (RN10).
    // ===============================================================
    @Transactional
    public void recalcularProgresso(Long necessidadeId) {
        Necessidade necessidade = buscarEntidade(necessidadeId);

        Integer somaRecebida = doacaoRepository.somarRecebidasDaNecessidade(necessidadeId);
        necessidade.setQuantidadeRecebida(somaRecebida == null ? 0 : somaRecebida);

        aplicarSituacaoPorProgresso(necessidade);
        necessidadeRepository.save(necessidade);
    }

    /**
     * RN07 - traduz quantidade recebida em situacao.
     *   recebida = 0                -> ABERTA
     *   0 < recebida < necessaria   -> PARCIALMENTE_ATENDIDA
     *   recebida >= necessaria      -> ATENDIDA  (pode passar de 100%, RN23)
     *   ENCERRADA                   -> continua ENCERRADA, sempre.
     */
    private void aplicarSituacaoPorProgresso(Necessidade necessidade) {

        if (necessidade.getSituacao() == SituacaoNecessidade.ENCERRADA) {
            return; // decisao administrativa vence o calculo automatico
        }

        int recebida = necessidade.getQuantidadeRecebida();
        int necessaria = necessidade.getQuantidadeNecessaria();

        if (recebida == 0) {
            necessidade.setSituacao(SituacaoNecessidade.ABERTA);
        } else if (recebida < necessaria) {
            necessidade.setSituacao(SituacaoNecessidade.PARCIALMENTE_ATENDIDA);
        } else {
            necessidade.setSituacao(SituacaoNecessidade.ATENDIDA);
        }
    }

    /** RN08 - usado pelo modulo de Doacoes antes de aceitar nova intencao. */
    @Transactional(readOnly = true)
    public Necessidade buscarDisponivelOuFalhar(Long id) {
        Necessidade necessidade = buscarEntidade(id);
        if (necessidade.getSituacao() == SituacaoNecessidade.ENCERRADA) {
            throw new RegraNegocioException("Este destino nao esta recebendo novas contribuicoes.");
        }
        return necessidade;
    }

    @Transactional(readOnly = true)
    public Necessidade buscarEntidade(Long id) {
        return necessidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Necessidade nao encontrada."));
    }
}
