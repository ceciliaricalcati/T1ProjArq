package br.edu.voo.domain.plano;

import br.edu.voo.domain.reserva.Intervalo;

import java.time.LocalDateTime;
import java.util.Optional;

/** Trecho com os horários que o sistema calcula (chegada). {@code ordem} é 1-based. */
public record TrechoCalculado(int ordem, Trecho trecho, LocalDateTime chegada) {

    /** Período em que a altitude da aerovia é usada. */
    public Intervalo ocupacaoAerovia() {
        return new Intervalo(trecho.entrada(), chegada);
    }

    /** Período em que a vaga é usada, se houver pouso. */
    public Optional<Intervalo> permanencia() {
        if (trecho.acao() instanceof Pouso pouso) {
            return Optional.of(new Intervalo(chegada, chegada.plusMinutes(pouso.permanenciaMin())));
        }
        return Optional.empty();
    }

    /** Horário em que a aeronave deve entrar na próxima aerovia. */
    public LocalDateTime saidaEsperada() {
        return permanencia().map(Intervalo::fim).orElse(chegada);
    }
}
