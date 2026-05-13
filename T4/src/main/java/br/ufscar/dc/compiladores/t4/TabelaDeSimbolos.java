package br.ufscar.dc.compiladores.t4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabela de símbolos usada pelo analisador semântico.
 * Armazena cada identificador declarado com seu tipo, categoria e informações extras.
 */
public class TabelaDeSimbolos {

    // -----------------------------------------------------------------------
    // Tipos de dados suportados pela linguagem LA
    // -----------------------------------------------------------------------
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
        VOID,            // tipo de retorno de procedimentos
        TIPO_INDEFINIDO  // usado quando ocorre erro semântico
    }

    // -----------------------------------------------------------------------
    // Categorias possíveis para um símbolo
    // -----------------------------------------------------------------------
    public enum CategoriaSimbolos {
        VARIAVEL,
        CONSTANTE,
        PROCEDIMENTO,
        FUNCAO,
        TIPO,
        PARAMETRO
    }

    // -----------------------------------------------------------------------
    // Estrutura que representa uma entrada na tabela de símbolos
    // -----------------------------------------------------------------------
    public static class EntradaTabelaDeSimbolos {
        public final String nome;
        public final TipoLA tipo;
        public final CategoriaSimbolos categoria;

        /** Para variáveis do tipo registro: guarda o nome do tipo (ex: "Ponto") */
        public final String nomeRegistro;

        /**
         * Para funções e procedimentos: lista dos tipos dos parâmetros formais,
         * na ordem em que foram declarados.
         */
        public final List<TipoLA> tiposParametros;

        /**
         * Para funções e procedimentos: lista dos nomeRegistro de cada parâmetro,
         * usado para verificar compatibilidade quando o tipo é REGISTRO.
         */
        public final List<String> nomesRegistroParametros;

        // Construtor completo (usado para funções/procedimentos)
        public EntradaTabelaDeSimbolos(String nome, TipoLA tipo,
                CategoriaSimbolos categoria, String nomeRegistro,
                List<TipoLA> tiposParametros, List<String> nomesRegistroParametros) {
            this.nome = nome;
            this.tipo = tipo;
            this.categoria = categoria;
            this.nomeRegistro = nomeRegistro;
            this.tiposParametros = tiposParametros != null ? tiposParametros : new ArrayList<>();
            this.nomesRegistroParametros = nomesRegistroParametros != null
                    ? nomesRegistroParametros : new ArrayList<>();
        }

        // Construtor simplificado (variáveis, constantes e tipos)
        public EntradaTabelaDeSimbolos(String nome, TipoLA tipo,
                CategoriaSimbolos categoria, String nomeRegistro) {
            this(nome, tipo, categoria, nomeRegistro, null, null);
        }
    }

    // -----------------------------------------------------------------------
    // Mapa interno: chave = nome do identificador
    // -----------------------------------------------------------------------
    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    /** Insere um símbolo simples (sem parâmetros). */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria) {
        adicionar(nome, tipo, categoria, null);
    }

    /** Insere um símbolo com nomeRegistro (registros e ponteiros de registro). */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria,
            String nomeRegistro) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, categoria, nomeRegistro));
    }

    /** Insere uma função ou procedimento com a lista de tipos de parâmetros. */
    public void adicionarFuncaoOuProcedimento(String nome, TipoLA tipoRetorno,
            CategoriaSimbolos categoria, List<TipoLA> tiposParametros,
            List<String> nomesRegistroParametros) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoRetorno,
                categoria, null, tiposParametros, nomesRegistroParametros));
    }

    /** Verifica se um nome já existe nesta tabela. */
    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    /** Retorna a entrada completa para um nome, ou null se não existir. */
    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }

    /** Retorna apenas o tipo de um símbolo; retorna TIPO_INDEFINIDO se não existir. */
    public TipoLA verificarTipo(String nome) {
        EntradaTabelaDeSimbolos e = tabela.get(nome);
        return (e != null) ? e.tipo : TipoLA.TIPO_INDEFINIDO;
    }
}