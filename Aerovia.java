package br.edu.voo.domain.rede;

import java.util.Objects;

/** Ligação orientada entre dois aeroportos. Imutável: não há operação de alteração. */
public record Aerovia(String id, Aeroporto origem, Aeroporto destino, int distanciaKm) {
    public Aerovia {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(origem, "origem");
        Objects.requireNonNull(destino, "destino");
        if (id.isBlank()) throw new IllegalArgumentException("Identificador da aerovia é obrigatório");
        if (origem.codigo().equals(destino.codigo()))
            throw new IllegalArgumentException("Origem e destino da aerovia devem ser diferentes");
        if (distanciaKm <= 0) throw new IllegalArgumentException("Distância deve ser positiva");
    }
}
