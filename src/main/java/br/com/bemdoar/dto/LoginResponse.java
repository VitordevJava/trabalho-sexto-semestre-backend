package br.com.bemdoar.dto;

public record LoginResponse(
        String token,
        Long id,
        String nome,
        String email,
        String perfil
) {
}
