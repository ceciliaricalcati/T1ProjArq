package br.edu.voo.domain;

/** Violação de uma invariante ou transição proibida do domínio (ex.: alterar plano aprovado). */
public class RegraDeDominioException extends RuntimeException {
    public RegraDeDominioException(String mensagem) {
        super(mensagem);
    }
}
