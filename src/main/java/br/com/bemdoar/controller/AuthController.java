package br.com.bemdoar.controller;

import br.com.bemdoar.dto.*;
import br.com.bemdoar.security.UsuarioAutenticado;
import br.com.bemdoar.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * RF01, RF02, RF03.
 * Esta classe ja esta pronta. NINGUEM precisa alterar este arquivo.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** POST /api/auth/cadastro - publico */
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid CadastroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    /** POST /api/auth/login - publico. Devolve o token JWT. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(usuarioService.autenticar(request));
    }

    /** GET /api/auth/eu - devolve os dados de quem esta logado. */
    @GetMapping("/eu")
    public ResponseEntity<UsuarioResponse> eu(@AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(usuarioService.verPerfil(logado.getId()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(
            @AuthenticationPrincipal UsuarioAutenticado logado,
            @RequestBody @Valid AtualizarPerfilRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(logado.getId(), request));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal UsuarioAutenticado logado,
            @RequestBody @Valid AlterarSenhaRequest request) {
        usuarioService.alterarSenha(logado.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/conta")
    public ResponseEntity<Void> desativarConta(@AuthenticationPrincipal UsuarioAutenticado logado) {
        usuarioService.desativarConta(logado.getId());
        return ResponseEntity.noContent().build();
    }
}
