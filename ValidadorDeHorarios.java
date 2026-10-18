package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.TrechoCalculado;

import java.util.ArrayList;
import java.util.List;

/** Entrada na 1ª aerovia = partida; entrada seguinte = chegada (+ permanência); chegada final no dia da partida. */
public class ValidadorDeHorarios implements Validador {

    @Override
    public List<Violacao> validar(PlanoDeVoo plano) {
        List<TrechoCalculado> cronograma = plano.cronograma();
        if (cronograma.isEmpty()) return List.of();

        List<Violacao> violacoes = new ArrayList<>();
        TrechoCalculado primeiro = cronograma.get(0);
        if (!primeiro.trecho().entrada().equals(plano.partida())) {
            violacoes.add(Violacao.doTrecho(1, CodigoViolacao.PARTIDA_INCOERENTE,
                    "A entrada na primeira aerovia (" + primeiro.trecho().entrada()
                            + ") deve ser igual à partida do plano (" + plano.partida() + ")"));
        }
        for (int i = 1; i < cronograma.size(); i++) {
            var anterior = cronograma.get(i - 1);
            var atual = cronograma.get(i);
            var esperado = anterior.saidaEsperada();
            if (!atual.trecho().entrada().equals(esperado)) {
                violacoes.add(Violacao.doTrecho(atual.ordem(), CodigoViolacao.HORARIO_INCOERENTE,
                        "Entrada informada " + atual.trecho().entrada() + ", mas o esperado é " + esperado
                                + " (chegada calculada do trecho " + anterior.ordem() + " mais a permanência, se houver)"));
            }
        }
        TrechoCalculado ultimo = cronograma.get(cronograma.size() - 1);
        if (!ultimo.chegada().toLocalDate().equals(plano.partida().toLocalDate())) {
            violacoes.add(Violacao.doTrecho(ultimo.ordem(), CodigoViolacao.VOO_EM_DIAS_DIFERENTES,
                    "A chegada ao destino final (" + ultimo.chegada() + ") deve ocorrer na data da partida"));
        }
        return violacoes;
    }
}
