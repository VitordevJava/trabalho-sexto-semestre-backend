package br.com.bemdoar.controller;

import br.com.bemdoar.dto.TransparenciaResponse;
import br.com.bemdoar.service.TransparenciaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transparencia")
public class TransparenciaController {

    private final TransparenciaService transparenciaService;

    public TransparenciaController(TransparenciaService transparenciaService) {
        this.transparenciaService = transparenciaService;
    }

    @GetMapping
    public TransparenciaResponse obter() {
        return transparenciaService.obter();
    }
}
