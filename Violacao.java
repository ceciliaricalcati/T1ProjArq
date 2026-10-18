package br.edu.voo.domain.validacao;

import java.util.Objects;

/** Um problema encontrado na validação. {@code ordemTrecho} é 1-based, ou null se for do plano. */
public record Violacao(CodigoViolacao codigo, String mensagem, Integer ordemTrecho) {
    public Violacao {
        Objects.requireNonNull(codigo, "codigo");
        Objects.requireNonNull(mensagem, "mensagem");
    }

    public static Violacao doPlano(CodigoViolacao codigo, String mensagem) {
        return new Violacao(codigo, mensagem, null);
    }

    public static Violacao doTrecho(int ordem, CodigoViolacao codigo, String mensagem) {
        return new Violacao(codigo, mensagem, ordem);
    }
}
