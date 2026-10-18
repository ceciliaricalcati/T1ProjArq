package br.edu.voo.domain.frota.regra;

import br.edu.voo.domain.reserva.Intervalo;
import br.edu.voo.domain.validacao.CodigoViolacao;
import br.edu.voo.domain.validacao.Violacao;

import java.util.List;
import java.util.Set;

public class RegraParticular implements RegraOperacao {
    private static final Set<Integer> ALTITUDES_PERMITIDAS = Set.of(25_000, 26_000, 27_000);

    @Override
    public List<Violacao> verificar(int ordemTrecho, int altitudePes, Intervalo ocupacaoNaAerovia) {
        if (ALTITUDES_PERMITIDAS.contains(altitudePes)) return List.of();
        return List.of(Violacao.doTrecho(ordemTrecho, CodigoViolacao.ALTITUDE_NAO_PERMITIDA_PARA_CATEGORIA,
                "Aeronave particular só opera a 25.000, 26.000 ou 27.000 pés (informado: " + altitudePes + ")"));
    }
}
