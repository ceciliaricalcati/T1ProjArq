package br.edu.voo.domain.plano;

/** Usa a vaga indicada durante a permanência e reabastece antes de prosseguir. Indicar a vaga não reserva. */
public record Pouso(int vaga, int permanenciaMin) implements AcaoNoAeroporto {
    public Pouso {
        if (vaga <= 0) throw new IllegalArgumentException("Vaga deve ser um número positivo");
        if (permanenciaMin <= 0) throw new IllegalArgumentException("Permanência deve ser positiva (minutos)");
    }
}
