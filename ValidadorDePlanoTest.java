package br.edu.voo.domain;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.Pouso;
import br.edu.voo.domain.plano.Sobrevoo;
import br.edu.voo.domain.plano.StatusPlano;
import br.edu.voo.domain.plano.Trecho;
import br.edu.voo.domain.validacao.CodigoViolacao;
import br.edu.voo.domain.validacao.ResultadoValidacao;
import br.edu.voo.domain.validacao.ValidadorDePlano;
import br.edu.voo.domain.validacao.Violacao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static br.edu.voo.domain.Fixtures.*;
import static br.edu.voo.domain.validacao.CodigoViolacao.*;
import static org.assertj.core.api.Assertions.assertThat;

class ValidadorDePlanoTest {
    private final Fixtures.OcupacaoEmMemoria livre = new Fixtures.OcupacaoEmMemoria();

    private static List<CodigoViolacao> codigos(ResultadoValidacao r) {
        return r.violacoes().stream().map(Violacao::codigo).toList();
    }

    private ResultadoValidacao validar(PlanoDeVoo plano) {
        return ValidadorDePlano.padrao(livre).validar(plano);
    }

    // ---------- caminho feliz ----------

    @Test
    void planoConsistenteComPousoIntermediarioEhValido() {
        var r = validar(planoValidoPoaCwb(passageiros(), 30_000, PARTIDA));
        assertThat(r.valido()).as(r.violacoes().toString()).isTrue();
    }

    @Test
    void sobrevooSemEsperaEhValidoQuandoEntradaIgualAChegada() {
        var a = passageiros();
        Trecho t1 = trecho(R1, 30_000, PARTIDA, new Sobrevoo());
        Trecho t2 = trecho(R4, 30_000, proximaEntrada(a, t1), new Pouso(5, 45));
        var r = validar(plano(a, POA, CWB, PARTIDA, t1, t2));
        assertThat(r.valido()).as(r.violacoes().toString()).isTrue();
    }

    @Test
    void validarNaoAlteraEstadoDoPlano() {
        var plano = planoValidoPoaCwb(passageiros(), 30_000, PARTIDA);
        validar(plano);
        assertThat(plano.status()).isEqualTo(StatusPlano.EM_EDICAO);
    }

    // ---------- percurso ----------

    @Test
    void planoSemTrechosEhInvalido() {
        var plano = new PlanoDeVoo(1L, PILOTO, passageiros(), POA, FLN, PARTIDA);
        assertThat(codigos(validar(plano))).containsExactly(SEM_TRECHOS);
    }

    @Test
    void aeroviasNaoContinuasSaoInvalidas() {
        var a = passageiros();
        Trecho t1 = trecho(R1, 30_000, PARTIDA, new Sobrevoo());          // POA -> FLN
        Trecho t2 = trecho(R3, 30_000, proximaEntrada(a, t1), new Pouso(1, 30)); // GRU -> POA
        var r = validar(plano(a, POA, POA, PARTIDA, t1, t2));
        assertThat(codigos(r)).contains(PERCURSO_DESCONTINUO);
    }

    @Test
    void primeiraAeroviaDeveSairDaOrigemEUltimaChegarAoDestinoDoPlano() {
        var a = passageiros();
        Trecho t = trecho(R1, 30_000, PARTIDA, new Pouso(1, 30)); // POA -> FLN
        var r = validar(plano(a, GRU, CWB, PARTIDA, t));
        assertThat(codigos(r)).contains(ORIGEM_INCOERENTE, DESTINO_INCOERENTE);
    }

    @Test
    void destinoFinalExigePouso() {
        var a = passageiros();
        Trecho t = trecho(R1, 30_000, PARTIDA, new Sobrevoo());
        var r = validar(plano(a, POA, FLN, PARTIDA, t));
        assertThat(codigos(r)).containsExactly(POUSO_FINAL_OBRIGATORIO);
    }

    // ---------- horários ----------

    @Test
    void entradaNaPrimeiraAeroviaDeveSerAPartidaDoPlano() {
        var a = passageiros();
        Trecho t = trecho(R1, 30_000, PARTIDA.plusMinutes(5), new Pouso(1, 30));
        var r = validar(plano(a, POA, FLN, PARTIDA, t));
        assertThat(codigos(r)).containsExactly(PARTIDA_INCOERENTE);
    }

    @Test
    void horarioIncompativelComADuracaoCalculadaEhInvalido() {
        var a = passageiros();
        Trecho t1 = trecho(R1, 30_000, PARTIDA, new Sobrevoo());
        // chegada calculada é 10:27:14; informar 10:20 (antes) é incoerente
        Trecho t2 = trecho(R4, 30_000, PARTIDA.plusMinutes(20), new Pouso(5, 45));
        var r = validar(plano(a, POA, CWB, PARTIDA, t1, t2));
        assertThat(codigos(r)).containsExactly(HORARIO_INCOERENTE);
    }

    @Test
    void aposPousoEntradaDeveConsiderarAPermanencia() {
        var a = passageiros();
        Trecho t1 = trecho(R1, 30_000, PARTIDA, new Pouso(3, 30));
        Trecho t2 = trecho(R4, 30_000, PARTIDA.plus(a.duracaoDe(R1)), new Pouso(5, 45)); // esqueceu a permanência
        var r = validar(plano(a, POA, CWB, PARTIDA, t1, t2));
        assertThat(codigos(r)).containsExactly(HORARIO_INCOERENTE);
    }

    @Test
    void chegadaNoDiaSeguinteEhInvalida_masPermanenciaFinalPodeCruzarAMeiaNoite() {
        var a = passageiros();
        // R2 (865 km) dura 3893 s (~1h05): saindo 23:30, chega 00:34 do dia seguinte -> inválido
        LocalDateTime tarde = LocalDateTime.of(2026, 11, 16, 23, 30);
        var invalido = plano(a, POA, GRU, tarde, trecho(R2, 30_000, tarde, new Pouso(1, 30)));
        assertThat(codigos(validar(invalido))).contains(VOO_EM_DIAS_DIFERENTES);

        // R1 (~27 min): saindo 23:00 chega 23:27 no mesmo dia; permanência de 90 min termina às 00:57 -> válido
        LocalDateTime noite = LocalDateTime.of(2026, 11, 16, 23, 0);
        var valido = plano(a, POA, FLN, noite, trecho(R1, 30_000, noite, new Pouso(1, 90)));
        assertThat(validar(valido).valido()).isTrue();
    }

    // ---------- aeronave ----------

    @Test
    void altitudeForaDaFaixaOuDoPassoEhInvalida() {
        var a = passageiros();
        Trecho t = trecho(R1, 36_000, PARTIDA, new Pouso(1, 30));
        assertThat(codigos(validar(plano(a, POA, FLN, PARTIDA, t)))).contains(ALTITUDE_INVALIDA);
        Trecho t2 = trecho(R1, 30_500, PARTIDA, new Pouso(1, 30));
        assertThat(codigos(validar(plano(a, POA, FLN, PARTIDA, t2)))).contains(ALTITUDE_INVALIDA);
    }

    @Test
    void particularNaoPodeVoarA30Mil() {
        var r = validar(planoValidoPoaCwb(particular(), 30_000, PARTIDA));
        assertThat(codigos(r)).contains(ALTITUDE_NAO_PERMITIDA_PARA_CATEGORIA);
    }

    @Test
    void particularPodeVoarA25Mil() {
        var r = validar(planoValidoPoaCwb(particular(), 25_000, PARTIDA));
        assertThat(r.valido()).as(r.violacoes().toString()).isTrue();
    }

    @Test
    void passageirosNaoPodeVoarA27Mil() {
        var r = validar(planoValidoPoaCwb(passageiros(), 27_000, PARTIDA));
        assertThat(codigos(r)).contains(ALTITUDE_NAO_PERMITIDA_PARA_CATEGORIA);
    }

    @Test
    void cargaSoOperaEntreMeiaNoiteESeisHoras() {
        var a = carga();
        LocalDateTime madrugada = LocalDateTime.of(2026, 11, 16, 1, 0);
        var ok = plano(a, POA, FLN, madrugada, trecho(R1, 25_000, madrugada, new Pouso(1, 30)));
        assertThat(validar(ok).valido()).isTrue();

        LocalDateTime tarde = LocalDateTime.of(2026, 11, 16, 5, 50); // 363 km a 700 km/h ~31 min -> chega 06:21
        var invalido = plano(a, POA, FLN, tarde, trecho(R1, 25_000, tarde, new Pouso(1, 30)));
        assertThat(codigos(validar(invalido))).containsExactly(HORARIO_NAO_PERMITIDO_PARA_CATEGORIA);
    }

    // ---------- autonomia ----------

    @Test
    void trechoUnicoQueUltrapassa85PorCentoEhInvalido() {
        var a = particular(); // limite 850 km; R2 tem 865 km
        Trecho t = trecho(R2, 25_000, PARTIDA, new Pouso(1, 30));
        var r = validar(plano(a, POA, GRU, PARTIDA, t));
        assertThat(codigos(r)).contains(AUTONOMIA_INSUFICIENTE);
    }

    @Test
    void sobrevooAcumulaDistanciaEPousoReiniciaOControle() {
        var curta = new br.edu.voo.domain.frota.Particular("PT-CUR", 300, 600, "Oficina"); // limite 510 km
        // R1 (363) + R4 (245) = 608 km sem pouso -> excede 510
        Trecho a1 = trecho(R1, 25_000, PARTIDA, new Sobrevoo());
        Trecho a2 = trecho(R4, 25_000, proximaEntrada(curta, a1), new Pouso(1, 30));
        assertThat(codigos(validar(plano(curta, POA, CWB, PARTIDA, a1, a2)))).contains(AUTONOMIA_INSUFICIENTE);

        // com pouso em FLN, cada perna (363 e 245) cabe no limite
        Trecho b1 = trecho(R1, 25_000, PARTIDA, new Pouso(1, 30));
        Trecho b2 = trecho(R4, 25_000, proximaEntrada(curta, b1), new Pouso(1, 30));
        var r = validar(plano(curta, POA, CWB, PARTIDA, b1, b2));
        assertThat(r.valido()).as(r.violacoes().toString()).isTrue();
    }

    // ---------- disponibilidade ----------

    @Test
    void altitudeJaOcupadaNoSlotGeraConflito() {
        var ocupacao = new Fixtures.OcupacaoEmMemoria().ocuparAltitude("R1", 30_000, PARTIDA); // 10:00
        var plano = planoValidoPoaCwb(passageiros(), 30_000, PARTIDA);
        var r = ValidadorDePlano.padrao(ocupacao).validar(plano);
        assertThat(codigos(r)).containsExactly(ALTITUDE_OCUPADA);
    }

    @Test
    void outraAltitudeDaMesmaAeroviaNaoConflita() {
        var ocupacao = new Fixtures.OcupacaoEmMemoria().ocuparAltitude("R1", 31_000, PARTIDA);
        var plano = planoValidoPoaCwb(passageiros(), 30_000, PARTIDA);
        assertThat(ValidadorDePlano.padrao(ocupacao).validar(plano).valido()).isTrue();
    }

    @Test
    void vagaOcupadaEmQualquerSlotDaPermanenciaGeraConflito() {
        // pouso em FLN: chegada 10:27:14, permanência 30 min -> slot 10h
        var ocupacao = new Fixtures.OcupacaoEmMemoria().ocuparVaga("FLN", 3, PARTIDA);
        var plano = planoValidoPoaCwb(passageiros(), 30_000, PARTIDA);
        var r = ValidadorDePlano.padrao(ocupacao).validar(plano);
        assertThat(codigos(r)).containsExactly(VAGA_OCUPADA);
    }

    @Test
    void vagaInexistenteNoAeroportoEhInvalida() {
        var a = passageiros();
        Trecho t = trecho(R1, 30_000, PARTIDA, new Pouso(17, 30)); // FLN tem 16 vagas
        var r = validar(plano(a, POA, FLN, PARTIDA, t));
        assertThat(codigos(r)).containsExactly(VAGA_INEXISTENTE);
    }

    @Test
    void validacaoDevolveTodasAsViolacoesDeUmaVez() {
        var a = particular();
        // altitude proibida, sem pouso final e partida incoerente ao mesmo tempo
        Trecho t = trecho(R1, 30_000, PARTIDA.plusMinutes(1), new Sobrevoo());
        var r = validar(plano(a, POA, FLN, PARTIDA, t));
        assertThat(codigos(r)).contains(POUSO_FINAL_OBRIGATORIO, PARTIDA_INCOERENTE, ALTITUDE_NAO_PERMITIDA_PARA_CATEGORIA);
    }
}
