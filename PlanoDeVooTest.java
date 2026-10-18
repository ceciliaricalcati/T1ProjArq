package br.edu.voo.domain;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.Pouso;
import br.edu.voo.domain.plano.StatusPlano;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static br.edu.voo.domain.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanoDeVooTest {
    private static final LocalDateTime ANTES = PARTIDA.minusDays(1);
    private static final LocalDateTime DEPOIS = PARTIDA.plusMinutes(1);

    private PlanoDeVoo novo() {
        return planoValidoPoaCwb(passageiros(), 30_000, PARTIDA);
    }

    @Test
    void nasceEmEdicao() {
        var plano = new PlanoDeVoo(null, PILOTO, passageiros(), POA, FLN, PARTIDA);
        assertThat(plano.status()).isEqualTo(StatusPlano.EM_EDICAO);
        assertThat(plano.trechos()).isEmpty();
    }

    @Test
    void cronogramaCalculaChegadaPelaDistanciaEVelocidade() {
        var cron = novo().cronograma();
        assertThat(cron.get(0).chegada()).isEqualTo(PARTIDA.plusSeconds(1634));
        assertThat(cron.get(0).saidaEsperada()).isEqualTo(PARTIDA.plusSeconds(1634).plusMinutes(30));
    }

    @Test
    void emEdicaoPodeAlterarPercursoEPartida() {
        var plano = novo();
        plano.definirPercurso(List.of());
        plano.alterarPartida(PARTIDA.plusHours(1));
        assertThat(plano.trechos()).isEmpty();
        assertThat(plano.partida()).isEqualTo(PARTIDA.plusHours(1));
    }

    @Test
    void aprovadoNaoPodeSerModificado() {
        var plano = novo();
        plano.aprovar();
        assertThatThrownBy(() -> plano.definirPercurso(List.of())).isInstanceOf(RegraDeDominioException.class);
        assertThatThrownBy(() -> plano.alterarPartida(DEPOIS)).isInstanceOf(RegraDeDominioException.class);
        assertThatThrownBy(plano::abandonar).isInstanceOf(RegraDeDominioException.class);
        assertThatThrownBy(plano::aprovar).isInstanceOf(RegraDeDominioException.class);
    }

    @Test
    void abandonadoNaoPodeSerModificadoNemAprovado() {
        var plano = novo();
        plano.abandonar();
        assertThat(plano.status()).isEqualTo(StatusPlano.ABANDONADO);
        assertThatThrownBy(plano::aprovar).isInstanceOf(RegraDeDominioException.class);
        assertThatThrownBy(() -> plano.definirPercurso(List.of())).isInstanceOf(RegraDeDominioException.class);
    }

    @Test
    void planoAprovadoFuturoPodeSerCancelado() {
        var plano = novo();
        plano.aprovar();
        plano.cancelar(ANTES);
        assertThat(plano.status()).isEqualTo(StatusPlano.CANCELADO);
    }

    @Test
    void planoAprovadoCujaPartidaJaOcorreuNaoPodeSerCancelado() {
        var plano = novo();
        plano.aprovar();
        assertThatThrownBy(() -> plano.cancelar(DEPOIS)).isInstanceOf(RegraDeDominioException.class);
        assertThatThrownBy(() -> plano.cancelar(PARTIDA)).isInstanceOf(RegraDeDominioException.class); // instante exato = já ocorreu
        assertThat(plano.status()).isEqualTo(StatusPlano.APROVADO);
    }

    @Test
    void planoEmEdicaoNaoPodeSerCancelado() {
        assertThatThrownBy(() -> novo().cancelar(ANTES)).isInstanceOf(RegraDeDominioException.class);
    }

    @Test
    void pousoExigeVagaEPermanenciaPositivas() {
        assertThatThrownBy(() -> new Pouso(0, 10)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Pouso(1, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
