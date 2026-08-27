package br.com.bemdoar.exception;

/**
 * Use quando o registro pedido nao existe no banco.
 * O TratadorDeErros transforma ela em HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
