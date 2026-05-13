package br.ufscar.dc.compiladores.t4;

import java.util.LinkedList;
import java.util.List;

/**
 * Gerencia a pilha de escopos durante a análise semântica.
 * Cada escopo corresponde a um bloco: global, procedimento ou função.
 * A pilha permite que escopos internos "enxerguem" símbolos dos externos.
 */
public class Escopos {

    // Pilha de tabelas: o topo é o escopo mais interno (atual)
    private final LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo(); // escopo global
    }

    /** Empurra um novo escopo vazio na pilha (ao entrar em função/procedimento). */
    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    /** Retorna o escopo mais interno sem removê-lo. */
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

    /** Remove o escopo mais interno da pilha (ao sair de função/procedimento). */
    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }

    /**
     * Procura um símbolo em todos os escopos, do mais interno para o mais externo.
     * Retorna a primeira entrada encontrada, ou null se não existir em nenhum escopo.
     */
    public TabelaDeSimbolos.EntradaTabelaDeSimbolos verificar(String nome) {
        for (TabelaDeSimbolos ts : pilhaDeTabelas) {
            if (ts.existe(nome)) {
                return ts.verificar(nome);
            }
        }
        return null;
    }

    /** Atalho: retorna true se o símbolo existe em qualquer nível de escopo. */
    public boolean existeEmQualquerEscopo(String nome) {
        return verificar(nome) != null;
    }

    /** Retorna toda a pilha para casos de inspeção (ex: busca em escopos específicos). */
    public List<TabelaDeSimbolos> percorrerEscopos() {
        return pilhaDeTabelas;
    }
}