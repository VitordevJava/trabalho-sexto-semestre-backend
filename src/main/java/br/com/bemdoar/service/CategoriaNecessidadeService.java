package br.com.bemdoar.service;

import br.com.bemdoar.dto.CategoriaRequest;
import br.com.bemdoar.dto.CategoriaResponse;
import br.com.bemdoar.entity.CategoriaNecessidade;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.CategoriaNecessidadeRepository;
import br.com.bemdoar.repository.NecessidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ======================= SERVICE - MOLDE =======================
 *
 * O SERVICE e onde ficam as REGRAS DE NEGOCIO.
 * O controller nao decide nada; o repository nao decide nada.
 * Toda pergunta do tipo "pode ou nao pode?" e respondida aqui.
 *
 * Padrao de todo metodo deste projeto:
 *   1. buscar o que precisa (repository)
 *   2. validar as regras (if -> throw new RegraNegocioException("mensagem"))
 *   3. alterar a entidade
 *   4. salvar
 *   5. devolver DTO
 *
 * PARA COPIAR: troque o nome da classe, do repository, da entidade e
 * dos DTOs. Mantenha a estrutura dos metodos.
 * ===============================================================
 */
@Service
public class CategoriaNecessidadeService {

    // Injecao por construtor: o Spring entrega os repositories sozinho.
    private final CategoriaNecessidadeRepository categoriaRepository;
    private final NecessidadeRepository necessidadeRepository;

    public CategoriaNecessidadeService(CategoriaNecessidadeRepository categoriaRepository,
                                       NecessidadeRepository necessidadeRepository) {
        this.categoriaRepository = categoriaRepository;
        this.necessidadeRepository = necessidadeRepository;
    }

    // ---------------------------------------------------------------
    // LISTAR (todas) - usado na area administrativa
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    // ---------------------------------------------------------------
    // LISTAR (so ativas) - usado nos formularios e na area publica
    // RN05: categoria inativa nao pode ser escolhida em cadastro novo.
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarAtivas() {
        return categoriaRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    // ---------------------------------------------------------------
    // BUSCAR UM
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return CategoriaResponse.de(buscarEntidade(id));
    }

    // ---------------------------------------------------------------
    // CRIAR
    // Regra RN05: o nome nao pode repetir, ignorando maiusculas.
    // ---------------------------------------------------------------
    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {

        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new RegraNegocioException("Ja existe uma categoria com este nome.");
        }

        CategoriaNecessidade categoria = new CategoriaNecessidade();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setAtivo(true); // toda categoria nova nasce ativa

        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    // ---------------------------------------------------------------
    // ATUALIZAR
    // Mesma regra do nome, mas ignorando o proprio registro.
    // ---------------------------------------------------------------
    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {

        CategoriaNecessidade categoria = buscarEntidade(id);

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new RegraNegocioException("Ja existe uma categoria com este nome.");
        }

        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());

        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    // ---------------------------------------------------------------
    // DESATIVAR (RN17 - preservacao historica)
    // Nao apaga: so impede o uso em cadastros novos.
    // ---------------------------------------------------------------
    @Transactional
    public CategoriaResponse desativar(Long id) {
        CategoriaNecessidade categoria = buscarEntidade(id);
        categoria.setAtivo(false);
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    // ---------------------------------------------------------------
    // REATIVAR
    // ---------------------------------------------------------------
    @Transactional
    public CategoriaResponse reativar(Long id) {
        CategoriaNecessidade categoria = buscarEntidade(id);
        categoria.setAtivo(true);
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    // ---------------------------------------------------------------
    // EXCLUIR DE VERDADE
    // RN17: so pode apagar fisicamente se NINGUEM estiver usando.
    // Se estiver em uso, a saida correta e desativar.
    // ---------------------------------------------------------------
    @Transactional
    public void excluir(Long id) {
        CategoriaNecessidade categoria = buscarEntidade(id);

        if (necessidadeRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException(
                    "Nao e possivel excluir esta categoria porque existem necessidades usando ela. Desative-a.");
        }

        categoriaRepository.delete(categoria);
    }

    // ---------------------------------------------------------------
    // Metodo interno reaproveitado por todos os outros.
    // Devolve a ENTIDADE (nao o DTO) e ja trata o "nao existe".
    // ---------------------------------------------------------------
    private CategoriaNecessidade buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria nao encontrada."));
    }

    /** Usado pelo NecessidadeService. RN05: precisa existir E estar ativa. */
    @Transactional(readOnly = true)
    public CategoriaNecessidade buscarAtivaOuFalhar(Long id) {
        CategoriaNecessidade categoria = buscarEntidade(id);
        if (!categoria.isAtivo()) {
            throw new RegraNegocioException("Selecione uma categoria ativa.");
        }
        return categoria;
    }
}
