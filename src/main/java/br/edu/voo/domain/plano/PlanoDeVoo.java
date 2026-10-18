package br.edu.voo.domain.plano;

import br.edu.voo.domain.RegraDeDominioException;
import br.edu.voo.domain.frota.Aeronave;
import br.edu.voo.domain.piloto.Piloto;
import br.edu.voo.domain.rede.Aeroporto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Agregado principal. Nasce em edição; só em edição pode ser modificado.
 * A consistência do percurso NÃO é exigida para modificar (o plano é construído progressivamente);
 * ela é verificada pela validação. As transições de estado ficam aqui para proteger as invariantes.
 */
public class PlanoDeVoo {
    private final Long id;
    private final Piloto piloto;
    private final Aeronave aeronave;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private LocalDateTime partida;
    private StatusPlano status;
    private List<Trecho> trechos;

    public PlanoDeVoo(Long id, Piloto piloto, Aeronave aeronave,
                      Aeroporto origem, Aeroporto destino, LocalDateTime partida) {
        this.id = id;
        this.piloto = Objects.requireNonNull(piloto, "piloto");
        this.aeronave = Objects.requireNonNull(aeronave, "aeronave");
        this.origem = Objects.requireNonNull(origem, "origem");
        this.destino = Objects.requireNonNull(destino, "destino");
        this.partida = Objects.requireNonNull(partida, "partida");
        this.status = StatusPlano.EM_EDICAO;
        this.trechos = List.of();
    }

    public Long id() { return id; }
    public Piloto piloto() { return piloto; }
    public Aeronave aeronave() { return aeronave; }
    public Aeroporto origem() { return origem; }
    public Aeroporto destino() { return destino; }
    public LocalDateTime partida() { return partida; }
    public StatusPlano status() { return status; }
    public List<Trecho> trechos() { return trechos; }

    // ---------- edição ----------

    public void alterarPartida(LocalDateTime novaPartida) {
        garantirEmEdicao("alterar a partida");
        this.partida = Objects.requireNonNull(novaPartida, "partida");
    }

    /** Substitui o percurso inteiro (aerovias, altitudes, entradas, sobrevoos, pousos e permanências). */
    public void definirPercurso(List<Trecho> novosTrechos) {
        garantirEmEdicao("alterar o percurso");
        this.trechos = List.copyOf(new ArrayList<>(novosTrechos));
    }

    // ---------- cálculo (leitura pura) ----------

    /** Chegada de cada trecho = entrada + distância / velocidade da aeronave. */
    public List<TrechoCalculado> cronograma() {
        List<TrechoCalculado> resultado = new ArrayList<>();
        int ordem = 1;
        for (Trecho t : trechos) {
            LocalDateTime chegada = t.entrada().plus(aeronave.duracaoDe(t.aerovia()));
            resultado.add(new TrechoCalculado(ordem++, t, chegada));
        }
        return List.copyOf(resultado);
    }

    // ---------- transições de estado ----------

    /**
     * Apenas muda o estado. Quem garante validação e reservas é o caso de uso de aprovação,
     * que executa tudo em uma única transação.
     */
    public void aprovar() {
        garantirEmEdicao("aprovar");
        this.status = StatusPlano.APROVADO;
    }

    /** Só plano aprovado cuja partida ainda não ocorreu (partida estritamente após {@code agora}). */
    public void cancelar(LocalDateTime agora) {
        if (status != StatusPlano.APROVADO) {
            throw new RegraDeDominioException("Somente plano aprovado pode ser cancelado (situação atual: " + status + ")");
        }
        if (!agora.isBefore(partida)) {
            throw new RegraDeDominioException("Plano cuja partida já ocorreu não pode ser cancelado");
        }
        this.status = StatusPlano.CANCELADO;
    }

    public void abandonar() {
        garantirEmEdicao("abandonar");
        this.status = StatusPlano.ABANDONADO;
    }

    private void garantirEmEdicao(String acao) {
        if (status != StatusPlano.EM_EDICAO) {
            throw new RegraDeDominioException(
                    "Não é possível " + acao + ": o plano está " + status + " e somente planos em edição podem ser modificados");
        }
    }
}
