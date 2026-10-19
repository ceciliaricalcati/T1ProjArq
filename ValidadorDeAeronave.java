package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.FaixaDeAltitude;
import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.TrechoCalculado;

import java.util.ArrayList;
import java.util.List;

/** Faixa global de altitudes + regra de operação da categoria (Strategy fornecida pela aeronave). */
public class ValidadorDeAeronave implements Validador {

    @Override
    public List<Violacao> validar(PlanoDeVoo plano) {
        List<Violacao> violacoes = new ArrayList<>();
        var regra = plano.aeronave().regraOperacao();
        for (TrechoCalculado c : plano.cronograma()) {
            int altitude = c.trecho().altitudePes();
            if (!FaixaDeAltitude.valida(altitude)) {
                violacoes.add(Violacao.doTrecho(c.ordem(), CodigoViolacao.ALTITUDE_INVALIDA,
                        "Altitude " + altitude + " inválida: use 25.000 a 35.000 pés em passos de 1.000"));
            }
            violacoes.addAll(regra.verificar(c.ordem(), altitude, c.ocupacaoAerovia()));
        }
        return violacoes;
    }
}
