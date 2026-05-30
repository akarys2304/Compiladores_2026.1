package br.ufscar.dc.compiladores.t5;

import java.util.LinkedList;
import java.util.List;

public class Escopos {

    private final LinkedList<TabelaDeSimbolos> pilha;

    public Escopos() {
        pilha = new LinkedList<>();
        criarNovoEscopo();
    }

    public void criarNovoEscopo() {
        pilha.push(new TabelaDeSimbolos());
    }

    public void abandonarEscopo() {
        pilha.pop();
    }

    public TabelaDeSimbolos obterEscopoAtual() {
        return pilha.peek();
    }

    public List<TabelaDeSimbolos> percorrerEscoposAninhados() {
        return pilha;
    }

    public TabelaDeSimbolos.EntradaTabelaDeSimbolos verificar(String nome) {

        for (TabelaDeSimbolos ts : pilha) {

            if (ts.existe(nome)) {
                return ts.verificar(nome);
            }
        }

        return null;
    }

    public boolean existeEmQualquerEscopo(String nome) {
        return verificar(nome) != null;
    }
}