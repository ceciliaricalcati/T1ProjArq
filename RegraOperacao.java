package br.edu.voo.domain.frota.regra;

import br.edu.voo.domain.reserva.Intervalo;
import br.edu.voo.domain.validacao.Violacao;

import java.util.List;

/** Strategy: regra de operação de uma categoria de aeronave sobre um trecho. */
public interface RegraOperacao {
    List<Violacao> verificar(int ordemTrecho, int altitudePes, Intervalo ocupacaoNaAerovia);
}
