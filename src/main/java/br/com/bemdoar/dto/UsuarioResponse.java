package br.com.bemdoar.dto;

import br.com.bemdoar.entity.Usuario;

import java.time.LocalDate;

/** Nunca inclui a senha. RN18. */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String perfil,
        boolean ativo
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNome(), u.getEmail(), u.getTelefone(),
                u.getDataNascimento(), u.getPerfil().name(), u.isAtivo());
    }
}
