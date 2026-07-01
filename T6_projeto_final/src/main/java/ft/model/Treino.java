package ft.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um treino (ex: "A", "B"), contendo uma lista ordenada de exercícios.
 */
public class Treino {

    private final String nome;
    private final List<Exercicio> exercicios;
    private final int linha;

    public Treino(String nome, int linha) {
        this.nome = nome;
        this.linha = linha;
        this.exercicios = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getLinha() {
        return linha;
    }

    public List<Exercicio> getExercicios() {
        return exercicios;
    }

    public void addExercicio(Exercicio e) {
        exercicios.add(e);
    }

    /** Soma o volume (séries x repetições) de todos os exercícios do treino. */
    public int volumeTotal() {
        int total = 0;
        for (Exercicio e : exercicios) {
            total += e.getVolume();
        }
        return total;
    }

    /** Soma total de séries do treino. */
    public int seriesTotais() {
        int total = 0;
        for (Exercicio e : exercicios) {
            total += e.getSeries();
        }
        return total;
    }
}
