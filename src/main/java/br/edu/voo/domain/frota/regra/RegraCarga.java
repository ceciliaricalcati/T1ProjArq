package br.edu.voo.domain.frota.regra;

import br.edu.voo.domain.reserva.Intervalo;
import br.edu.voo.domain.validacao.CodigoViolacao;
import br.edu.voo.domain.validacao.Violacao;

import java.time.LocalTime;
import java.util.List;

/** Todo o uso da aerovia deve estar em [00:00, 06:00) do mesmo dia; chegada às 06:00 exatas é inválida. */
public class RegraCarga implements RegraOperacao {
    private static final LocalTime FIM_DA_JANELA = LocalTime.of(6, 0);

    @Override
    public List<Violacao> verificar(int ordemTrecho, int altitudePes, Intervalo ocupacaoNaAerovia) {
        boolean mesmoDia = ocupacaoNaAerovia.inicio().toLocalDate().equals(ocupacaoNaAerovia.fim().toLocalDate());
        boolean antesDasSeis = ocupacaoNaAerovia.fim().toLocalTime().isBefore(FIM_DA_JANELA);
        if (mesmoDia && antesDasSeis) return List.of();
        return List.of(Violacao.doTrecho(ordemTrecho, CodigoViolacao.HORARIO_NAO_PERMITIDO_PARA_CATEGORIA,
                "Aeronave de carga só pode usar a aerovia entre 00:00 (inclusive) e 06:00 (exclusive); "
                        + "uso de " + ocupacaoNaAerovia.inicio() + " a " + ocupacaoNaAerovia.fim()));
    }
}
