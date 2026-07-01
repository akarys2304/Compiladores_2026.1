package ft.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um aluno, com atributos opcionais (idade, peso, objetivo)
 * e uma lista ordenada de treinos.
 */
public class Aluno {

    private final String nome;
    private final int linha;

    private Integer idade;     // opcional
    private Double peso;       // opcional
    private String objetivo;   // opcional

    private final List<Treino> treinos;

    public Aluno(String nome, int linha) {
        this.nome = nome;
        this.linha = linha;
        this.treinos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public int getLinha() {
        return linha;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public List<Treino> getTreinos() {
        return treinos;
    }

    public void addTreino(Treino t) {
        treinos.add(t);
    }
}
