package br.edu.voo.domain.frota;

import br.edu.voo.domain.frota.regra.RegraOperacao;
import br.edu.voo.domain.frota.regra.RegraParticular;

public class Particular extends Aeronave {
    private static final RegraOperacao REGRA = new RegraParticular();
    private final String empresaManutencao;

    public Particular(String prefixo, int velocidadeCruzeiroKmh, int autonomiaKm, String empresaManutencao) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        this.empresaManutencao = empresaManutencao;
    }

    public String empresaManutencao() { return empresaManutencao; }

    @Override
    public RegraOperacao regraOperacao() { return REGRA; }
}
