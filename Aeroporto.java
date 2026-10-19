package br.edu.voo.domain.rede;

import java.util.Objects;

public record Aeroporto(String codigo, String nome, int quantidadeVagas) {
    public Aeroporto {
        Objects.requireNonNull(codigo, "codigo");
        Objects.requireNonNull(nome, "nome");
        if (codigo.isBlank()) throw new IllegalArgumentException("Código do aeroporto é obrigatório");
        if (quantidadeVagas <= 0) throw new IllegalArgumentException("Quantidade de vagas deve ser positiva");
    }
}
