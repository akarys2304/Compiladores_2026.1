package br.ufscar.dc.compiladores.t3; 

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela de símbolos que armazena identificadores e seus tipos/categorias
 * dentro de um escopo específico.
 *
 * Cada entrada contém o nome do símbolo, sua categoria semântica
 * (variável, constante, procedimento, função, tipo) e o tipo da LA
 * (inteiro, real, literal, lógico, registro, ponteiro, etc.).
 */
public class TabelaDeSimbolos {

    /**
     * Tipos primitivos e compostos da Linguagem Algorítmica.
     * TIPO_INDEFINIDO é usado quando uma expressão tem tipos incompatíveis.
     */
    public enum TipoLA {
        INTEIRO,
        REAL,
        LITERAL,
        LOGICO,
        REGISTRO,
        PONTEIRO_INTEIRO,
        PONTEIRO_REAL,
        PONTEIRO_LITERAL,
        PONTEIRO_LOGICO,
        PONTEIRO_REGISTRO,
        VOID,             // para procedimentos (sem retorno)
        TIPO_INDEFINIDO   // resultado de operação inválida
    }

    /**
     * Categorias de identificadores reconhecidas pelo compilador.
     */
    public enum CategoriaSimbolos {
        VARIAVEL,
        CONSTANTE,
        PROCEDIMENTO,
        FUNCAO,
        TIPO
    }

    /**
     * Entrada individual na tabela de símbolos.
     */
    public static class EntradaTabelaDeSimbolos {
        public final String nome;
        public final TipoLA tipo;
        public final CategoriaSimbolos categoria;
        // Para tipos REGISTRO definidos pelo usuário, armazena o nome do tipo
        public final String nomeRegistro;

        public EntradaTabelaDeSimbolos(String nome, TipoLA tipo,
                CategoriaSimbolos categoria, String nomeRegistro) {
            this.nome = nome;
            this.tipo = tipo;
            this.categoria = categoria;
            this.nomeRegistro = nomeRegistro;
        }
    }

    // Mapa principal: nome → entrada
    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    /**
     * Insere um símbolo na tabela (sem nome de registro).
     */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria) {
        adicionar(nome, tipo, categoria, null);
    }

    /**
     * Insere um símbolo na tabela com nome de registro associado.
     * Usado para distinguir registros de tipos diferentes.
     */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria, String nomeRegistro) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, categoria, nomeRegistro));
    }

    /**
     * Verifica se um símbolo existe nesta tabela.
     */
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    /**
     * Retorna a entrada completa de um símbolo ou null se não existir.
     */
    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }

    /**
     * Retorna apenas o tipo de um símbolo já existente.
     * Deve ser chamado após confirmar que o símbolo existe.
     */
    public TipoLA verificarTipo(String nome) {
        EntradaTabelaDeSimbolos e = tabela.get(nome);
        return (e != null) ? e.tipo : TipoLA.TIPO_INDEFINIDO;
    }
}