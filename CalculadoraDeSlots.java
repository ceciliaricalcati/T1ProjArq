package br.edu.voo.domain.reserva;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Converte um intervalo nas horas cheias que ele toca. Qualquer uso parcial ocupa o slot inteiro.
 * O fim é exclusivo: 15:00 a 16:00 ocupa somente o slot das 15h. Um intervalo de duração zero
 * ocupa o slot que contém o instante.
 */
public final class CalculadoraDeSlots {

    private CalculadoraDeSlots() {
    }

    public static List<Slot> slotsDe(Intervalo intervalo) {
        LocalDateTime primeiro = intervalo.inicio().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime limite = intervalo.fim().isAfter(intervalo.inicio())
                ? tetoEmHoraCheia(intervalo.fim())
                : primeiro.plusHours(1);
        List<Slot> slots = new ArrayList<>();
        for (LocalDateTime s = primeiro; s.isBefore(limite); s = s.plusHours(1)) {
            slots.add(new Slot(s));
        }
        return List.copyOf(slots);
    }

    private static LocalDateTime tetoEmHoraCheia(LocalDateTime t) {
        LocalDateTime piso = t.truncatedTo(ChronoUnit.HOURS);
        return piso.equals(t) ? t : piso.plusHours(1);
    }
}
