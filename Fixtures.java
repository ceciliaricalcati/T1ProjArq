package br.edu.voo.domain;

import br.edu.voo.domain.frota.Aeronave;
import br.edu.voo.domain.frota.ComercialCarga;
import br.edu.voo.domain.frota.ComercialPassageiros;
import br.edu.voo.domain.frota.Particular;
import br.edu.voo.domain.piloto.Piloto;
import br.edu.voo.domain.plano.AcaoNoAeroporto;
import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.Pouso;
import br.edu.voo.domain.plano.Trecho;
import br.edu.voo.domain.rede.Aeroporto;
import br.edu.voo.domain.rede.Aerovia;
import br.edu.voo.domain.reserva.ConsultaDeOcupacao;
import br.edu.voo.domain.reserva.Slot;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/** Rede inicial do enunciado e utilitários de teste. */
final class Fixtures {
    static final Aeroporto POA = new Aeroporto("POA", "Salgado Filho", 41);
    static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    static final Aeroporto CWB = new Aeroporto("CWB", "Afonso Pena", 26);
    static final Aeroporto GRU = new Aeroporto("GRU", "Guarulhos", 115);

    static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    static final Aerovia R2 = new Aerovia("R2", POA, GRU, 865);
    static final Aerovia R3 = new Aerovia("R3", GRU, POA, 865);
    static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);
    static final Aerovia R5 = new Aerovia("R5", CWB, FLN, 245);
    static final Aerovia R6 = new Aerovia("R6", CWB, GRU, 360);
    static final Aerovia R7 = new Aerovia("R7", GRU, CWB, 360);
    static final Aerovia R8 = new Aerovia("R8", FLN, POA, 363);

    static final LocalDateTime PARTIDA = LocalDateTime.of(2026, 11, 16, 10, 0);
    static final Piloto PILOTO = new Piloto(1L, "Ana");

    private Fixtures() {
    }

    /** 800 km/h, autonomia 2.000 km (limite 1.700 km). */
    static Aeronave passageiros() {
        return new ComercialPassageiros("PR-PAX", 800, 2000, "Cia Sul", 180);
    }

    /** 300 km/h, autonomia 1.000 km (limite 850 km). */
    static Aeronave particular() {
        return new Particular("PT-ABC", 300, 1000, "Oficina Sul");
    }

    /** 700 km/h, autonomia 3.000 km. */
    static Aeronave carga() {
        return new ComercialCarga("PR-CGO", 700, 3000, "Cargo Sul", 20.0);
    }

    static Trecho trecho(Aerovia aerovia, int altitude, LocalDateTime entrada, AcaoNoAeroporto acao) {
        return new Trecho(aerovia, altitude, entrada, acao);
    }

    /** Entrada esperada do trecho seguinte: entrada + duração calculada + permanência (se pouso). */
    static LocalDateTime proximaEntrada(Aeronave aeronave, Trecho t) {
        LocalDateTime chegada = t.entrada().plus(aeronave.duracaoDe(t.aerovia()));
        return t.acao() instanceof Pouso p ? chegada.plusMinutes(p.permanenciaMin()) : chegada;
    }

    static PlanoDeVoo plano(Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                            LocalDateTime partida, Trecho... trechos) {
        PlanoDeVoo plano = new PlanoDeVoo(1L, PILOTO, aeronave, origem, destino, partida);
        plano.definirPercurso(java.util.List.of(trechos));
        return plano;
    }

    /** POA -> FLN (pouso, vaga 3, 30 min) -> CWB (pouso, vaga 5, 45 min), tudo consistente. */
    static PlanoDeVoo planoValidoPoaCwb(Aeronave aeronave, int altitude, LocalDateTime partida) {
        Trecho t1 = trecho(R1, altitude, partida, new Pouso(3, 30));
        Trecho t2 = trecho(R4, altitude, proximaEntrada(aeronave, t1), new Pouso(5, 45));
        return plano(aeronave, POA, CWB, partida, t1, t2);
    }

    /** Ocupação em memória para testar disponibilidade sem banco. */
    static final class OcupacaoEmMemoria implements ConsultaDeOcupacao {
        private final Set<String> ocupados = new HashSet<>();

        OcupacaoEmMemoria ocuparAltitude(String aerovia, int altitude, LocalDateTime horaCheia) {
            ocupados.add("A|" + aerovia + "|" + altitude + "|" + horaCheia);
            return this;
        }

        OcupacaoEmMemoria ocuparVaga(String aeroporto, int vaga, LocalDateTime horaCheia) {
            ocupados.add("V|" + aeroporto + "|" + vaga + "|" + horaCheia);
            return this;
        }

        @Override
        public boolean altitudeOcupada(String aeroviaId, int altitudePes, Slot slot) {
            return ocupados.contains("A|" + aeroviaId + "|" + altitudePes + "|" + slot.inicio());
        }

        @Override
        public boolean vagaOcupada(String codigoAeroporto, int vaga, Slot slot) {
            return ocupados.contains("V|" + codigoAeroporto + "|" + vaga + "|" + slot.inicio());
        }
    }
}
