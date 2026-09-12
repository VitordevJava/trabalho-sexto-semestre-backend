package br.com.bemdoar.dto;
import br.com.bemdoar.enums.TipoComunicado; import jakarta.validation.constraints.*;
public record ComunicadoRequest(@NotBlank String titulo,@NotBlank String conteudo,@NotNull TipoComunicado tipo) {}
