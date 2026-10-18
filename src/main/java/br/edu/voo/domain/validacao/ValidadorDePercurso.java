package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.Pouso;
import br.edu.voo.domain.plano.Trecho;

import java.util.ArrayList;
import java.util.List;

/** Existência de trechos, coerência com origem/destino do plano, continuidade e pouso final. */
public class ValidadorDePercurso implements Validador {

    @Override
    public List<Violacao> validar(PlanoDeVoo plano) {
        List<Trecho> trechos = plano.trechos();
        if (trechos.isEmpty()) {
            return List.of(Violacao.doPlano(CodigoViolacao.SEM_TRECHOS, "O plano precisa de pelo menos um trecho"));
        }
        List<Violacao> violacoes = new ArrayList<>();
        Trecho primeiro = trechos.get(0);
        Trecho ultimo = trechos.get(trechos.size() - 1);

        if (!primeiro.aerovia().origem().codigo().equals(plano.origem().codigo())) {
            violacoes.add(Violacao.doTrecho(1, CodigoViolacao.ORIGEM_INCOERENTE,
                    "A primeira aerovia parte de " + primeiro.aerovia().origem().codigo()
                            + ", mas a origem do plano é " + plano.origem().codigo()));
        }
        if (!ultimo.aerovia().destino().codigo().equals(plano.destino().codigo())) {
            violacoes.add(Violacao.doTrecho(trechos.size(), CodigoViolacao.DESTINO_INCOERENTE,
                    "A última aerovia chega a " + ultimo.aerovia().destino().codigo()
                            + ", mas o destino do plano é " + plano.destino().codigo()));
        }
        for (int i = 0; i < trechos.size() - 1; i++) {
            String chegada = trechos.get(i).aerovia().destino().codigo();
            String proximaOrigem = trechos.get(i + 1).aerovia().origem().codigo();
            if (!chegada.equals(proximaOrigem)) {
                violacoes.add(Violacao.doTrecho(i + 2, CodigoViolacao.PERCURSO_DESCONTINUO,
                        "O trecho " + (i + 1) + " chega a " + chegada + ", mas o trecho " + (i + 2)
                                + " parte de " + proximaOrigem));
            }
        }
        if (!(ultimo.acao() instanceof Pouso)) {
            violacoes.add(Violacao.doTrecho(trechos.size(), CodigoViolacao.POUSO_FINAL_OBRIGATORIO,
                    "No destino final deve ocorrer pouso"));
        }
        return violacoes;
    }
}
