package br.com.bemdoar.controller;

import br.com.bemdoar.dto.CategoriaRequest;
import br.com.bemdoar.dto.CategoriaResponse;
import br.com.bemdoar.service.CategoriaNecessidadeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================== CONTROLLER - MOLDE =====================
 *
 * O CONTROLLER so faz tres coisas:
 *   1. define a URL   (@RequestMapping + @GetMapping/@PostMapping/...)
 *   2. define quem pode entrar (@PreAuthorize)
 *   3. chama o service e devolve a resposta
 *
 * NAO coloque if de regra de negocio aqui. Isso e trabalho do service.
 *
 * Tabela de anotacoes que voce vai usar:
 *   @GetMapping      -> ler          -> 200 OK
 *   @PostMapping     -> criar        -> 201 CREATED
 *   @PutMapping      -> atualizar    -> 200 OK
 *   @PatchMapping    -> mudar estado -> 200 OK
 *   @DeleteMapping   -> apagar       -> 204 NO CONTENT
 *
 * PARA COPIAR: troque a rota do @RequestMapping, o nome da classe,
 * o service e os DTOs. Mantenha as anotacoes e os codigos de status.
 * ==============================================================
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaNecessidadeService categoriaService;

    public CategoriaController(CategoriaNecessidadeService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET /api/categorias/ativas  -> PUBLICO (liberado no SecurityConfig)
    @GetMapping("/ativas")
    public ResponseEntity<List<CategoriaResponse>> listarAtivas() {
        return ResponseEntity.ok(categoriaService.listarAtivas());
    }

    // GET /api/categorias  -> so ADMINISTRADOR
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CategoriaResponse>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    // GET /api/categorias/1
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // POST /api/categorias
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> criar(@RequestBody @Valid CategoriaRequest request) {
        CategoriaResponse criada = categoriaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // PUT /api/categorias/1
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    // PATCH /api/categorias/1/desativar
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> desativar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desativar(id));
    }

    // PATCH /api/categorias/1/reativar
    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CategoriaResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.reativar(id));
    }

    // DELETE /api/categorias/1
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
