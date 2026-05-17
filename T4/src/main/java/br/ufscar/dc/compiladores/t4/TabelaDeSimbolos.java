package br.ufscar.dc.compiladores.t4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabelaDeSimbolos {

    public enum TipoLA {
        INTEIRO, REAL, LITERAL, LOGICO, REGISTRO,
        PONTEIRO_INTEIRO, PONTEIRO_REAL, PONTEIRO_LITERAL, PONTEIRO_LOGICO, PONTEIRO_REGISTRO,
        VOID, TIPO_INDEFINIDO
    }

    public enum CategoriaSimbolos {
        VARIAVEL, CONSTANTE, PROCEDIMENTO, FUNCAO, TIPO, PARAMETRO
    }

    public static class EntradaTabelaDeSimbolos {
        public final String nome;
        public final TipoLA tipo;
        public final CategoriaSimbolos categoria;
        public final String nomeRegistro;

        /** Campos do registro: nome → tipo. Null para não-registros. */
        public final Map<String, TipoLA> camposRegistro;

        /** Tipos dos parâmetros formais (para funções e procedimentos). */
        public final List<TipoLA> tiposParametros;

        public EntradaTabelaDeSimbolos(String nome, TipoLA tipo, CategoriaSimbolos categoria,
                String nomeRegistro, Map<String, TipoLA> camposRegistro,
                List<TipoLA> tiposParametros) {
            this.nome = nome;
            this.tipo = tipo;
            this.categoria = categoria;
            this.nomeRegistro = nomeRegistro;
            this.camposRegistro = camposRegistro;
            this.tiposParametros = tiposParametros != null ? tiposParametros : new ArrayList<>();
        }
    }

    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    /** Insere variável simples. */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria) {
        adicionar(nome, tipo, categoria, null, null);
    }

    /** Insere variável com nomeRegistro e campos. */
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria,
            String nomeRegistro, Map<String, TipoLA> camposRegistro) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, categoria,
                nomeRegistro, camposRegistro, null));
    }

    /** Insere função ou procedimento com lista de tipos de parâmetros. */
    public void adicionarFuncaoOuProcedimento(String nome, TipoLA tipoRetorno,
            CategoriaSimbolos categoria, List<TipoLA> tiposParametros,
            List<String> nomesRegParam) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipoRetorno, categoria,
                null, null, tiposParametros));
    }

    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }

    public TipoLA verificarTipo(String nome) {
        EntradaTabelaDeSimbolos e = tabela.get(nome);
        return (e != null) ? e.tipo : TipoLA.TIPO_INDEFINIDO;
    }
}