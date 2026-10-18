package br.edu.voo.domain.frota;

import br.edu.voo.domain.frota.regra.RegraCarga;
import br.edu.voo.domain.frota.regra.RegraOperacao;

public class ComercialCarga extends Aeronave {
    private static final RegraOperacao REGRA = new RegraCarga();
    private final String companhia;
    private final double capacidadeCargaToneladas;

    public ComercialCarga(String prefixo, int velocidadeCruzeiroKmh, int autonomiaKm,
                          String companhia, double capacidadeCargaToneladas) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        this.companhia = companhia;
        this.capacidadeCargaToneladas = capacidadeCargaToneladas;
    }

    public String companhia() { return companhia; }

    public double capacidadeCargaToneladas() { return capacidadeCargaToneladas; }

    @Override
    public RegraOperacao regraOperacao() { return REGRA; }
}
