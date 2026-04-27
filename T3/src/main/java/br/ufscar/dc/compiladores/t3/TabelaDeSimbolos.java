package br.ufscar.dc.compiladores.t3; 

import java.util.HashMap;
import java.util.Map;


public class TabelaDeSimbolos {

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
        VOID,            
        TIPO_INDEFINIDO  
    }

    
    public enum CategoriaSimbolos {
        VARIAVEL,
        CONSTANTE,
        PROCEDIMENTO,
        FUNCAO,
        TIPO
    }

    
    public static class EntradaTabelaDeSimbolos {
        public final String nome;
        public final TipoLA tipo;
        public final CategoriaSimbolos categoria;
    
        public final String nomeRegistro;

        public EntradaTabelaDeSimbolos(String nome, TipoLA tipo,
                CategoriaSimbolos categoria, String nomeRegistro) {
            this.nome = nome;
            this.tipo = tipo;
            this.categoria = categoria;
            this.nomeRegistro = nomeRegistro;
        }
    }


    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new HashMap<>();
    }

    
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria) {
        adicionar(nome, tipo, categoria, null);
    }

    
    public void adicionar(String nome, TipoLA tipo, CategoriaSimbolos categoria, String nomeRegistro) {
        tabela.put(nome, new EntradaTabelaDeSimbolos(nome, tipo, categoria, nomeRegistro));
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