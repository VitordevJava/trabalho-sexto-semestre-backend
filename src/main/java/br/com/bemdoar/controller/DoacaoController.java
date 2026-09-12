package br.com.bemdoar.controller;

import br.com.bemdoar.dto.CancelamentoRequest;
import br.com.bemdoar.dto.DoacaoRequest;
import br.com.bemdoar.dto.DoacaoResponse;
import br.com.bemdoar.enums.SituacaoDoacao;
import br.com.bemdoar.security.UsuarioAutenticado;
import br.com.bemdoar.service.DoacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoacaoResponse criar(@Valid @RequestBody DoacaoRequest request,
                                Authentication authentication) {
        return doacaoService.criar(usuarioId(authentication), request);
    }

    @GetMapping("/minhas")
    public List<DoacaoResponse> listarMinhas(Authentication authentication) {
        return doacaoService.listarMinhas(usuarioId(authentication));
    }

    @GetMapping("/{id}")
    public DoacaoResponse buscar(@PathVariable Long id, Authentication authentication) {
        return doacaoService.buscar(id, usuarioId(authentication), ehAdministrador(authentication));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<DoacaoResponse> listarTodas(
            @RequestParam(required = false) SituacaoDoacao situacao) {
        return doacaoService.listarTodas(situacao);
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public DoacaoResponse confirmar(@PathVariable Long id) {
        return doacaoService.confirmar(id);
    }

    @PatchMapping("/{id}/receber")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public DoacaoResponse receber(@PathVariable Long id) {
        return doacaoService.receber(id);
    }

    @PatchMapping("/{id}/cancelar")
    public DoacaoResponse cancelar(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) CancelamentoRequest request,
                                   Authentication authentication) {
        return doacaoService.cancelar(
                id, usuarioId(authentication), ehAdministrador(authentication), request);
    }

    private Long usuarioId(Authentication authentication) {
        return ((UsuarioAutenticado) authentication.getPrincipal()).getId();
    }

    private boolean ehAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
}
