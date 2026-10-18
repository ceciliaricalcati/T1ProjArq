package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.plano.Pouso;
import br.edu.voo.domain.plano.TrechoCalculado;
import br.edu.voo.domain.reserva.CalculadoraDeSlots;
import br.edu.voo.domain.reserva.ConsultaDeOcupacao;
import br.edu.voo.domain.reserva.Slot;

import java.util.ArrayList;
import java.util.List;

/** Altitude livre em todos os slots da aerovia; vaga livre em todos os slots da permanência. Não reserva. */
public class ValidadorDeDisponibilidade implements Validador {
    private final ConsultaDeOcupacao ocupacao;

    public ValidadorDeDisponibilidade(ConsultaDeOcupacao ocupacao) {
        this.ocupacao = ocupacao;
    }

    @Override
    public List<Violacao> validar(PlanoDeVoo plano) {
        List<Violacao> violacoes = new ArrayList<>();
        for (TrechoCalculado c : plano.cronograma()) {
            var aerovia = c.trecho().aerovia();
            int altitude = c.trecho().altitudePes();

            List<Slot> slotsAerovia = CalculadoraDeSlots.slotsDe(c.ocupacaoAerovia());
            List<Slot> ocupados = slotsAerovia.stream()
                    .filter(s -> ocupacao.altitudeOcupada(aerovia.id(), altitude, s)).toList();
            if (!ocupados.isEmpty()) {
                violacoes.add(Violacao.doTrecho(c.ordem(), CodigoViolacao.ALTITUDE_OCUPADA,
                        "Altitude " + altitude + " da aerovia " + aerovia.id() + " ocupada nos slots: "
                                + ocupados.stream().map(s -> s.inicio().toString()).toList()));
            }

            if (c.trecho().acao() instanceof Pouso pouso) {
                var aeroporto = aerovia.destino();
                if (pouso.vaga() > aeroporto.quantidadeVagas()) {
                    violacoes.add(Violacao.doTrecho(c.ordem(), CodigoViolacao.VAGA_INEXISTENTE,
                            "O aeroporto " + aeroporto.codigo() + " possui " + aeroporto.quantidadeVagas()
                                    + " vagas; vaga " + pouso.vaga() + " não existe"));
                } else {
                    List<Slot> slotsVaga = CalculadoraDeSlots.slotsDe(c.permanencia().orElseThrow());
                    List<Slot> vagasOcupadas = slotsVaga.stream()
                            .filter(s -> ocupacao.vagaOcupada(aeroporto.codigo(), pouso.vaga(), s)).toList();
                    if (!vagasOcupadas.isEmpty()) {
                        violacoes.add(Violacao.doTrecho(c.ordem(), CodigoViolacao.VAGA_OCUPADA,
                                "Vaga " + pouso.vaga() + " de " + aeroporto.codigo() + " ocupada nos slots: "
                                        + vagasOcupadas.stream().map(s -> s.inicio().toString()).toList()));
                    }
                }
            }
        }
        return violacoes;
    }
}
