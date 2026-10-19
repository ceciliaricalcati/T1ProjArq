package br.edu.voo.domain.reserva;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Hora cheia [inicio, inicio + 1h). Unidade de controle de ocupação de aerovias e vagas. */
public record Slot(LocalDateTime inicio) implements Comparable<Slot> {
    public Slot {
        Objects.requireNonNull(inicio, "inicio");
        if (!inicio.equals(inicio.truncatedTo(ChronoUnit.HOURS)))
            throw new IllegalArgumentException("Slot deve começar em hora cheia: " + inicio);
    }

    public static Slot contendo(LocalDateTime instante) {
        return new Slot(instante.truncatedTo(ChronoUnit.HOURS));
    }

    public LocalDateTime fim() {
        return inicio.plusHours(1);
    }

    @Override
    public int compareTo(Slot outro) {
        return inicio.compareTo(outro.inicio);
    }
}
