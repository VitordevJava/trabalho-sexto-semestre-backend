package br.com.bemdoar.repository;

import br.com.bemdoar.entity.Usuario;
import br.com.bemdoar.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    /** RN03 - usado ao ALTERAR o e-mail: ignora o proprio registro. */
    boolean existsByEmailAndIdNot(String email, Long id);

    List<Usuario> findByPerfilAndAtivoTrue(PerfilUsuario perfil);
}
