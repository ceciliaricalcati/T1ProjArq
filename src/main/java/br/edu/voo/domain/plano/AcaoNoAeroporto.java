package br.edu.voo.domain.plano;

/** O que ocorre ao chegar ao aeroporto de destino de um trecho: sobrevoo ou pouso. */
public sealed interface AcaoNoAeroporto permits Sobrevoo, Pouso {
    default boolean pousa() {
        return this instanceof Pouso;
    }
}
