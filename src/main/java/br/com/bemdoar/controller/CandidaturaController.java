package br.com.bemdoar.controller;

import br.com.bemdoar.dto.CandidaturaResponse;
import br.com.bemdoar.dto.ParticipacaoRequest;
import br.com.bemdoar.security.UsuarioAutenticado;
import br.com.bemdoar.service.CandidaturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * Todas as rotas aqui exigem usuario logado (nao ha nenhuma liberada
 * no SecurityConfig) - dentro disso, /aprovar, /recusar e a listagem
 * de candidatos por oportunidade exigem ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    // POST /api/candidaturas?oportunidadeId=1 - USUARIO LOGADO
    @PostMapping
    public ResponseEntity<CandidaturaResponse> candidatar(
            @RequestParam Long oportunidadeId,
            @AuthenticationPrincipal UsuarioAutenticado logado) {
        CandidaturaResponse criada = candidaturaService.candidatar(logado.getId(), oportunidadeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // GET /api/candidaturas/minhas - USUARIO LOGADO (RN18 - so as proprias)
    @GetMapping("/minhas")
    public ResponseEntity<List<CandidaturaResponse>> minhas(@AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(candidaturaService.listarMinhas(logado.getId()));
    }

    // PATCH /api/candidaturas/1/aprovar - ADMINISTRADOR
    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CandidaturaResponse> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(candidaturaService.aprovar(id));
    }

    // PATCH /api/candidaturas/1/recusar?motivo=... - ADMINISTRADOR
    @PatchMapping("/{id}/recusar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CandidaturaResponse> recusar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(candidaturaService.recusar(id, motivo));
    }

    // PATCH /api/candidaturas/1/cancelar - DONO OU ADMINISTRADOR (validado no Service)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CandidaturaResponse> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado logado) {
        return ResponseEntity.ok(candidaturaService.cancelar(id, logado));
    }

    // POST /api/candidaturas/1/participacao - ADMINISTRADOR
    @PostMapping("/{id}/participacao")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CandidaturaResponse> registrarParticipacao(
            @PathVariable Long id,
            @RequestBody @Valid ParticipacaoRequest request) {
        return ResponseEntity.ok(candidaturaService.registrarParticipacao(id, request));
    }
}
