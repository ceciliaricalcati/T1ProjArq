package br.edu.voo.domain.frota;

import br.edu.voo.domain.frota.regra.RegraOperacao;
import br.edu.voo.domain.rede.Aerovia;

import java.time.Duration;
import java.util.Objects;

/**
 * Aeronave abstrata. Cada categoria define sua {@link RegraOperacao}; adicionar uma categoria
 * é criar uma subclasse e uma regra, sem alterar validadores nem o plano.
 */
public abstract class Aeronave {
    public static final int PERCENTUAL_UTILIZAVEL_DA_AUTONOMIA = 85;

    private final String prefixo;
    private final int velocidadeCruzeiroKmh;
    private final int autonomiaKm;

    protected Aeronave(String prefixo, int velocidadeCruzeiroKmh, int autonomiaKm) {
        Objects.requireNonNull(prefixo, "prefixo");
        if (prefixo.isBlank()) throw new IllegalArgumentException("Prefixo é obrigatório");
        if (velocidadeCruzeiroKmh <= 0) throw new IllegalArgumentException("Velocidade deve ser positiva");
        if (autonomiaKm <= 0) throw new IllegalArgumentException("Autonomia deve ser positiva");
        this.prefixo = prefixo;
        this.velocidadeCruzeiroKmh = velocidadeCruzeiroKmh;
        this.autonomiaKm = autonomiaKm;
    }

    public String prefixo() { return prefixo; }

    public int velocidadeCruzeiroKmh() { return velocidadeCruzeiroKmh; }

    public int autonomiaKm() { return autonomiaKm; }

    /** distância / velocidade, em segundos inteiros, arredondando para cima (ADR de arredondamento). */
    public Duration duracaoDe(Aerovia aerovia) {
        long segundos = Math.ceilDiv(aerovia.distanciaKm() * 3600L, (long) velocidadeCruzeiroKmh);
        return Duration.ofSeconds(segundos);
    }

    /** Limite sem reabastecer: 85% da autonomia nominal. */
    public double limiteSemReabastecerKm() {
        return autonomiaKm * PERCENTUAL_UTILIZAVEL_DA_AUTONOMIA / 100.0;
    }

    /** Comparação em inteiros, sem erro de ponto flutuante. */
    public boolean excedeAutonomia(int distanciaAcumuladaKm) {
        return distanciaAcumuladaKm * 100L > autonomiaKm * (long) PERCENTUAL_UTILIZAVEL_DA_AUTONOMIA;
    }

    public abstract RegraOperacao regraOperacao();
}
