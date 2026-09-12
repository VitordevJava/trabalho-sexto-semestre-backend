package br.com.bemdoar.dto;

import org.springframework.data.domain.Page;
import java.util.List;

/** Contrato estavel de paginacao, independente da serializacao interna do Spring. */
public record PaginaResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> PaginaResponse<T> de(Page<T> pagina) {
        return new PaginaResponse<>(pagina.getContent(), pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages(), pagina.isFirst(), pagina.isLast());
    }
}
