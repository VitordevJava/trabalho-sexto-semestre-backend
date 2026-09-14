package br.com.bemdoar.controller;

import br.com.bemdoar.dto.CandidaturaResponse;
import br.com.bemdoar.dto.OportunidadeRequest;
import br.com.bemdoar.dto.OportunidadeResponse;
import br.com.bemdoar.service.CandidaturaService;
import br.com.bemdoar.service.OportunidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FRENTE 3 - VOLUNTARIADO.
 * GET / e GET /{id} sao PUBLICOS (liberados no SecurityConfig).
 * O restante exige ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/oportunidades")
public class OportunidadeController {

    private final OportunidadeService oportunidadeService;
    private final CandidaturaService candidaturaService;

    public OportunidadeController(OportunidadeService oportunidadeService,
                                  CandidaturaService candidaturaService) {
        this.oportunidadeService = oportunidadeService;
        this.candidaturaService = candidaturaService;
    }

    // GET /api/oportunidades - PUBLICO
    @GetMapping
    public ResponseEntity<List<OportunidadeResponse>> listarTodas() {
        return ResponseEntity.ok(oportunidadeService.listarTodas());
    }

    // GET /api/oportunidades/1 - PUBLICO
    @GetMapping("/{id}")
    public ResponseEntity<OportunidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadeService.buscarPorId(id));
    }

    // POST /api/oportunidades - ADMINISTRADOR
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<OportunidadeResponse> criar(@RequestBody @Valid OportunidadeRequest request) {
        OportunidadeResponse criada = oportunidadeService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // PUT /api/oportunidades/1 - ADMINISTRADOR
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<OportunidadeResponse> atualizar(@PathVariable Long id,
                                                          @RequestBody @Valid OportunidadeRequest request) {
        return ResponseEntity.ok(oportunidadeService.atualizar(id, request));
    }

    // PATCH /api/oportunidades/1/abrir - ADMINISTRADOR
    @PatchMapping("/{id}/abrir")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<OportunidadeResponse> abrir(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadeService.abrir(id));
    }

    // PATCH /api/oportunidades/1/encerrar - ADMINISTRADOR
    @PatchMapping("/{id}/encerrar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<OportunidadeResponse> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(oportunidadeService.encerrar(id));
    }

    // GET /api/oportunidades/1/candidaturas - ADMINISTRADOR
    @GetMapping("/{id}/candidaturas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CandidaturaResponse>> listarCandidatos(@PathVariable Long id) {
        return ResponseEntity.ok(candidaturaService.listarPorOportunidade(id));
    }
}
