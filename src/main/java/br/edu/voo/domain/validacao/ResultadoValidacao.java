package br.edu.voo.domain.validacao;

import java.util.List;

public record ResultadoValidacao(List<Violacao> violacoes) {
    public ResultadoValidacao {
        violacoes = List.copyOf(violacoes);
    }

    public boolean valido() {
        return violacoes.isEmpty();
    }
}
