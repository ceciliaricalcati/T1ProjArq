package br.edu.voo.domain.reserva;

import java.time.LocalDateTime;
import java.util.Objects;

/** Intervalo de tempo [inicio, fim]. O fim pode coincidir com o início (instante). */
public record Intervalo(LocalDateTime inicio, LocalDateTime fim) {
    public Intervalo {
        Objects.requireNonNull(inicio, "inicio");
        Objects.requireNonNull(fim, "fim");
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("Fim anterior ao início");
    }
}
