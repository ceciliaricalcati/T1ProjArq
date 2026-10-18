package br.edu.voo.domain;

import org.junit.jupiter.api.Test;

import static br.edu.voo.domain.Fixtures.R1;
import static br.edu.voo.domain.Fixtures.passageiros;
import static br.edu.voo.domain.Fixtures.particular;
import static org.assertj.core.api.Assertions.assertThat;

class AeronaveTest {

    @Test
    void duracaoArredondaParaCimaEmSegundos() {
        // 363 km a 800 km/h = 1633,5 s -> 1634 s
        assertThat(passageiros().duracaoDe(R1).getSeconds()).isEqualTo(1634);
    }

    @Test
    void duracaoExataNaoArredonda() {
        // 300 km/h: 363 km = 4356 s exatos
        assertThat(particular().duracaoDe(R1).getSeconds()).isEqualTo(4356);
    }

    @Test
    void limiteDeAutonomiaE85PorCento() {
        var a = particular(); // autonomia 1000 -> limite 850
        assertThat(a.limiteSemReabastecerKm()).isEqualTo(850.0);
        assertThat(a.excedeAutonomia(850)).isFalse();
        assertThat(a.excedeAutonomia(851)).isTrue();
    }
}
