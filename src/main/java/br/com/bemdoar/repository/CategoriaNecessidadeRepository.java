package br.com.bemdoar.repository;

import br.com.bemdoar.entity.CategoriaNecessidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * REPOSITORY-MOLDE.
 *
 * O Spring Data cria a implementacao sozinho a partir do NOME do metodo.
 * Voce so declara a assinatura. Nao existe corpo, nao existe SQL.
 */
public interface CategoriaNecessidadeRepository extends JpaRepository<CategoriaNecessidade, Long> {

    /** RN05 - nome unico ignorando maiusculas/minusculas (usado ao CRIAR). */
    boolean existsByNomeIgnoreCase(String nome);

    /** RN05 - mesma checagem ao EDITAR, ignorando o proprio registro. */
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<CategoriaNecessidade> findByAtivoTrueOrderByNomeAsc();

    List<CategoriaNecessidade> findAllByOrderByNomeAsc();
}
