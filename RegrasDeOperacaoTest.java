package br.edu.voo.domain;

import br.edu.voo.domain.frota.regra.RegraCarga;
import br.edu.voo.domain.frota.regra.RegraParticular;
import br.edu.voo.domain.frota.regra.RegraPassageiros;
import br.edu.voo.domain.reserva.Intervalo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RegrasDeOperacaoTest {
    private static LocalDateTime t(int h, int m) {
        return LocalDateTime.of(2026, 11, 16, h, m);
    }

    private static final Intervalo QUALQUER = new Intervalo(t(10, 0), t(11, 0));

    @Test
    void particularSoOperaEm25_26_27Mil() {
        var regra = new RegraParticular();
        assertThat(regra.verificar(1, 25_000, QUALQUER)).isEmpty();
        assertThat(regra.verificar(1, 27_000, QUALQUER)).isEmpty();
        assertThat(regra.verificar(1, 28_000, QUALQUER)).hasSize(1);
    }

    @Test
    void passageirosExige28MilOuMais() {
        var regra = new RegraPassageiros();
        assertThat(regra.verificar(1, 27_000, QUALQUER)).hasSize(1);
        assertThat(regra.verificar(1, 28_000, QUALQUER)).isEmpty();
        assertThat(regra.verificar(1, 35_000, QUALQUER)).isEmpty();
    }

    @Test
    void cargaAceitaMeiaNoiteInclusiveESeisHorasExclusive() {
        var regra = new RegraCarga();
        assertThat(regra.verificar(1, 30_000, new Intervalo(t(0, 0), t(0, 30)))).isEmpty();
        assertThat(regra.verificar(1, 30_000, new Intervalo(t(5, 0), t(5, 59)))).isEmpty();
        assertThat(regra.verificar(1, 30_000, new Intervalo(t(5, 0), t(6, 0)))).hasSize(1);
        assertThat(regra.verificar(1, 30_000, new Intervalo(t(5, 30), t(6, 10)))).hasSize(1);
    }

    @Test
    void cargaNaoPodeCruzarAMeiaNoite() {
        var regra = new RegraCarga();
        var cruza = new Intervalo(t(23, 40), LocalDateTime.of(2026, 11, 17, 0, 10));
        assertThat(regra.verificar(1, 30_000, cruza)).hasSize(1);
    }
}
