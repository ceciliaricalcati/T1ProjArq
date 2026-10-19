package br.edu.voo.domain.validacao;

import br.edu.voo.domain.plano.PlanoDeVoo;
import br.edu.voo.domain.reserva.ConsultaDeOcupacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Executa a cadeia de validadores e devolve TODAS as violações (não para na primeira).
 * Novas regras entram como novos {@link Validador}s, sem alterar os existentes.
 */
public class ValidadorDePlano {
    private final List<Validador> validadores;

    public ValidadorDePlano(List<Validador> validadores) {
        this.validadores = List.copyOf(validadores);
    }

    public static ValidadorDePlano padrao(ConsultaDeOcupacao ocupacao) {
        return new ValidadorDePlano(List.of(
                new ValidadorDePercurso(),
                new ValidadorDeHorarios(),
                new ValidadorDeAeronave(),
                new ValidadorDeAutonomia(),
                new ValidadorDeDisponibilidade(ocupacao)));
    }

    public ResultadoValidacao validar(PlanoDeVoo plano) {
        List<Violacao> todas = new ArrayList<>();
        for (Validador v : validadores) {
            todas.addAll(v.validar(plano));
        }
        return new ResultadoValidacao(todas);
    }
}
