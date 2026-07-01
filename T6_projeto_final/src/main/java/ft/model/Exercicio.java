package ft.model;

/**
 * Representa um exercício dentro de um treino, com nome, número de séries
 * e número de repetições por série.
 */
public class Exercicio {

    private final String nome;
    private final int series;
    private final int repeticoes;
    private final int linha; // linha do código-fonte, usada em mensagens de erro

    public Exercicio(String nome, int series, int repeticoes, int linha) {
        this.nome = nome;
        this.series = series;
        this.repeticoes = repeticoes;
        this.linha = linha;
    }

    public String getNome() {
        return nome;
    }

    public int getSeries() {
        return series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public int getLinha() {
        return linha;
    }

    /** Volume total do exercício (séries x repetições). */
    public int getVolume() {
        return series * repeticoes;
    }
}
