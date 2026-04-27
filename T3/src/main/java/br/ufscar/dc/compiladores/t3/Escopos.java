package br.ufscar.dc.compiladores.t3;

import java.util.LinkedList;
import java.util.List;

public class Escopos {

    private final LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo();
    }

    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

   
    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }

    
    public TabelaDeSimbolos.EntradaTabelaDeSimbolos verificar(String nome) {
        for (TabelaDeSimbolos ts : pilhaDeTabelas) {
            if (ts.existe(nome)) {
                return ts.verificar(nome);
            }
        }
        return null;
    }

    public boolean existeEmQualquerEscopo(String nome) {
        return verificar(nome) != null;
    }

    
    public List<TabelaDeSimbolos> percorrerEscopos() {
        return pilhaDeTabelas;
    }
}