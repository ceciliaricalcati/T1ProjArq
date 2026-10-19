package br.edu.voo.domain;

import br.edu.voo.domain.reserva.CalculadoraDeSlots;
import br.edu.voo.domain.reserva.Intervalo;
import br.edu.voo.domain.reserva.Slot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculadoraDeSlotsTest {
    private static LocalDateTime t(int h, int m) {
        return LocalDateTime.of(2026, 11, 16, h, m);
    }

    private static List<LocalDateTime> inicios(Intervalo i) {
        return CalculadoraDeSlots.slotsDe(i).stream().map(Slot::inicio).toList();
    }

    @Test
    void exemploDoEnunciadoParaAerovia_15h52_ate_17h07_ocupaTresSlots() {
        assertThat(inicios(new Intervalo(t(15, 52), t(17, 7)))).containsExactly(t(15, 0), t(16, 0), t(17, 0));
    }

    @Test
    void exemploDoEnunciadoParaVaga_chegada15h50_permanencia20min_ocupaDoisSlots() {
        assertThat(inicios(new Intervalo(t(15, 50), t(15, 50).plusMinutes(20)))).containsExactly(t(15, 0), t(16, 0));
    }

    @Test
    void fimExatamenteNaHoraCheiaNaoOcupaSlotExtra() {
        assertThat(inicios(new Intervalo(t(15, 0), t(16, 0)))).containsExactly(t(15, 0));
    }

    @Test
    void usoParcialDentroDeUmaHoraOcupaOSlotInteiro() {
        assertThat(inicios(new Intervalo(t(15, 10), t(15, 40)))).containsExactly(t(15, 0));
    }

    @Test
    void intervaloDeDuracaoZeroOcupaOSlotDoInstante() {
        assertThat(inicios(new Intervalo(t(15, 0), t(15, 0)))).containsExactly(t(15, 0));
        assertThat(inicios(new Intervalo(t(15, 30), t(15, 30)))).containsExactly(t(15, 0));
    }

    @Test
    void ocupacaoQueCruzaAMeiaNoiteGeraSlotsDoDiaSeguinte() {
        var slots = inicios(new Intervalo(t(23, 30), LocalDateTime.of(2026, 11, 17, 0, 30)));
        assertThat(slots).containsExactly(t(23, 0), LocalDateTime.of(2026, 11, 17, 0, 0));
    }

    @Test
    void slotExigeHoraCheia() {
        assertThatThrownBy(() -> new Slot(t(15, 30))).isInstanceOf(IllegalArgumentException.class);
    }
}
