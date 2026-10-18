package br.edu.voo.domain.reserva;

/** Porta do domínio: consulta o que já está reservado. Implementada pela infraestrutura (banco). */
public interface ConsultaDeOcupacao {
    boolean altitudeOcupada(String aeroviaId, int altitudePes, Slot slot);

    boolean vagaOcupada(String codigoAeroporto, int vaga, Slot slot);
}
