package br.edu.voo.domain.frota.regra;

import br.edu.voo.domain.reserva.Intervalo;
import br.edu.voo.domain.validacao.CodigoViolacao;
import br.edu.voo.domain.validacao.Violacao;

import java.util.List;

/** Altitude mínima de 28.000 pés. O teto da faixa global é verificado pelo ValidadorDeAeronave. */
public class RegraPassageiros implements RegraOperacao {
    private static final int ALTITUDE_MINIMA = 28_000;

    @Override
    public List<Violacao> verificar(int ordemTrecho, int altitudePes, Intervalo ocupacaoNaAerovia) {
        if (altitudePes >= ALTITUDE_MINIMA) return List.of();
        return List.of(Violacao.doTrecho(ordemTrecho, CodigoViolacao.ALTITUDE_NAO_PERMITIDA_PARA_CATEGORIA,
                "Aeronave comercial de passageiros exige altitude >= 28.000 pés (informado: " + altitudePes + ")"));
    }
}
