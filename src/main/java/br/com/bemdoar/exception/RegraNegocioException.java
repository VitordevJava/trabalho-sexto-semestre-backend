package br.com.bemdoar.exception;

/**
 * Use esta excecao sempre que uma REGRA DE NEGOCIO for violada.
 * Ex: "A quantidade deve ser maior que zero", "Ja existe uma categoria com este nome".
 *
 * O TratadorDeErros transforma ela em HTTP 400 com a mensagem que voce escreveu.
 * Voce NUNCA precisa montar o JSON de erro na mao.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
