package br.edu.voo.domain.plano;

/** Altitudes disponíveis: 25.000 a 35.000 pés, inclusive, em passos de 1.000. */
public final class FaixaDeAltitude {
    public static final int MINIMA = 25_000;
    public static final int MAXIMA = 35_000;
    public static final int PASSO = 1_000;

    private FaixaDeAltitude() {
    }

    public static boolean valida(int altitudePes) {
        return altitudePes >= MINIMA && altitudePes <= MAXIMA && (altitudePes - MINIMA) % PASSO == 0;
    }
}
