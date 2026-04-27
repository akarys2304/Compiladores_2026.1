package br.ufscar.dc.compiladores.t3;

import java.util.LinkedList;
import java.util.List;

/**
 * Gerencia a pilha de escopos aninhados do compilador.
 *
 * Cada chamada de procedimento ou função cria um novo escopo.
 * A busca por um símbolo percorre os escopos do mais interno
 * (topo da pilha) ao mais externo (global).
 */
public class Escopos {

    // Pilha de tabelas de símbolos (topo = escopo atual)
    private final LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        // O primeiro escopo criado é o global
        criarNovoEscopo();
    }

    /**
     * Cria um novo escopo (ao entrar em procedimento/função).
     */
    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    /**
     * Retorna a tabela de símbolos do escopo mais interno (atual).
     */
    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek();
    }

    /**
     * Remove o escopo mais interno (ao sair de procedimento/função).
     */
    public void abandonarEscopo() {
        pilhaDeTabelas.pop();
    }

    /**
     * Busca um símbolo percorrendo do escopo mais interno ao mais externo.
     * Retorna a entrada encontrada ou null se não existir em nenhum escopo.
     */
    public TabelaDeSimbolos.EntradaTabelaDeSimbolos verificar(String nome) {
        for (TabelaDeSimbolos ts : pilhaDeTabelas) {
            if (ts.existe(nome)) {
                return ts.verificar(nome);
            }
        }
        return null;
    }

    /**
     * Verifica se um símbolo existe em algum escopo acessível.
     */
    public boolean existeEmQualquerEscopo(String nome) {
        return verificar(nome) != null;
    }

    /**
     * Retorna a lista completa de escopos (usado para percorrer todos).
     */
    public List<TabelaDeSimbolos> percorrerEscopos() {
        return pilhaDeTabelas;
    }
}