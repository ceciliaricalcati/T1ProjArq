package br.edu.voo.domain.frota;

import br.edu.voo.domain.frota.regra.RegraOperacao;
import br.edu.voo.domain.frota.regra.RegraPassageiros;

public class ComercialPassageiros extends Aeronave {
    private static final RegraOperacao REGRA = new RegraPassageiros();
    private final String companhia;
    private final int capacidadePassageiros;

    public ComercialPassageiros(String prefixo, int velocidadeCruzeiroKmh, int autonomiaKm,
                                String companhia, int capacidadePassageiros) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        this.companhia = companhia;
        this.capacidadePassageiros = capacidadePassageiros;
    }

    public String companhia() { return companhia; }

    public int capacidadePassageiros() { return capacidadePassageiros; }

    @Override
    public RegraOperacao regraOperacao() { return REGRA; }
}
