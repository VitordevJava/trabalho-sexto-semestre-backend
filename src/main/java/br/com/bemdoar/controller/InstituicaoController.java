package br.com.bemdoar.controller;

import br.com.bemdoar.dto.InstituicaoRequest;
import br.com.bemdoar.dto.InstituicaoResponse;
import br.com.bemdoar.service.InstituicaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** RF04. */
@RestController
@RequestMapping("/api/instituicao")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    /** GET /api/instituicao - PUBLICO */
    @GetMapping
    public ResponseEntity<InstituicaoResponse> consultar() {
        return ResponseEntity.ok(instituicaoService.consultar());
    }

    /** PUT /api/instituicao - so ADMINISTRADOR */
    @PutMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InstituicaoResponse> atualizar(@RequestBody @Valid InstituicaoRequest request) {
        return ResponseEntity.ok(instituicaoService.atualizar(request));
    }
}
