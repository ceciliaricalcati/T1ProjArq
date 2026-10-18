package br.edu.voo.domain.piloto;

import java.util.Objects;

public record Piloto(Long id, String nome) {
    public Piloto {
        Objects.requireNonNull(nome, "nome");
        if (nome.isBlank()) throw new IllegalArgumentException("Nome do piloto é obrigatório");
    }
}
