package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;

import java.util.List;

/** Uma regra de validação com responsabilidade única. Leitura pura: nunca altera o plano. */
public interface Validador {
    List<Violacao> validar(PlanoDeVoo plano);
}
