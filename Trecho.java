package br.edu.voo.domain.plano;

import br.edu.voo.domain.rede.Aerovia;

import java.time.LocalDateTime;
import java.util.Objects;

/** Uso de uma aerovia: altitude, horário de entrada informado pelo usuário e ação ao chegar ao destino. */
public record Trecho(Aerovia aerovia, int altitudePes, LocalDateTime entrada, AcaoNoAeroporto acao) {
    public Trecho {
        Objects.requireNonNull(aerovia, "aerovia");
        Objects.requireNonNull(entrada, "entrada");
        Objects.requireNonNull(acao, "acao");
    }
}
