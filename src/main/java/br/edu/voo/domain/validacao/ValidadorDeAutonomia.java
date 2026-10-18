package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.TrechoCalculado;

import java.util.ArrayList;
import java.util.List;

/** Distância desde o último abastecimento <= 85% da autonomia. Sobrevoo acumula; pouso zera. */
public class ValidadorDeAutonomia implements Validador {

    @Override
    public List<Violacao> validar(PlanoDeVoo plano) {
        List<Violacao> violacoes = new ArrayList<>();
        var aeronave = plano.aeronave();
        int acumuladoKm = 0;
        for (TrechoCalculado c : plano.cronograma()) {
            acumuladoKm += c.trecho().aerovia().distanciaKm();
            if (aeronave.excedeAutonomia(acumuladoKm)) {
                violacoes.add(Violacao.doTrecho(c.ordem(), CodigoViolacao.AUTONOMIA_INSUFICIENTE,
                        "Distância desde o último abastecimento: " + acumuladoKm + " km; limite: "
                                + aeronave.limiteSemReabastecerKm() + " km (85% de " + aeronave.autonomiaKm() + " km)"));
            }
            if (c.trecho().acao().pousa()) {
                acumuladoKm = 0;
            }
        }
        return violacoes;
    }
}
