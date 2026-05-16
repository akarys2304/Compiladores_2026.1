package br.ufscar.dc.compiladores.t4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        TIPO,
        PARAMETRO
    }

    public static class EntradaTabelaDeSimbolos {

        public final String nome;
        public final TipoLA tipo;
        public final CategoriaSimbolos categoria;

        public final String nomeRegistro;

        public final List<TipoLA> tiposParametros;

        public final List<String> nomesRegistroParametros;

        public final Map<String, TipoLA> camposRegistro;

        public EntradaTabelaDeSimbolos(
                String nome,
                TipoLA tipo,
                CategoriaSimbolos categoria,
                String nomeRegistro,
                List<TipoLA> tiposParametros,
                List<String> nomesRegistroParametros,
                Map<String, TipoLA> camposRegistro
        ) {

            this.nome = nome;
            this.tipo = tipo;
            this.categoria = categoria;
            this.nomeRegistro = nomeRegistro;

            this.tiposParametros =
                    tiposParametros != null
                            ? tiposParametros
                            : new ArrayList<>();

            this.nomesRegistroParametros =
                    nomesRegistroParametros != null
                            ? nomesRegistroParametros
                            : new ArrayList<>();

            this.camposRegistro =
                    camposRegistro != null
                            ? camposRegistro
                            : new HashMap<>();
        }
    }

    private final Map<String, EntradaTabelaDeSimbolos> tabela;

    public TabelaDeSimbolos() {
        tabela = new HashMap<>();
    }

    public void adicionar(
            String nome,
            TipoLA tipo,
            CategoriaSimbolos categoria
    ) {

        adicionar(
                nome,
                tipo,
                categoria,
                null,
                null
        );
    }

    public void adicionar(
            String nome,
            TipoLA tipo,
            CategoriaSimbolos categoria,
            String nomeRegistro
    ) {

        adicionar(
                nome,
                tipo,
                categoria,
                nomeRegistro,
                null
        );
    }

    public void adicionar(
            String nome,
            TipoLA tipo,
            CategoriaSimbolos categoria,
            String nomeRegistro,
            Map<String, TipoLA> camposRegistro
    ) {

        tabela.put(
                nome,
                new EntradaTabelaDeSimbolos(
                        nome,
                        tipo,
                        categoria,
                        nomeRegistro,
                        null,
                        null,
                        camposRegistro
                )
        );
    }

    public void adicionarFuncaoOuProcedimento(
            String nome,
            TipoLA tipoRetorno,
            CategoriaSimbolos categoria,
            List<TipoLA> tiposParametros,
            List<String> nomesRegistroParametros
    ) {

        tabela.put(
                nome,
                new EntradaTabelaDeSimbolos(
                        nome,
                        tipoRetorno,
                        categoria,
                        null,
                        tiposParametros,
                        nomesRegistroParametros,
                        null
                )
        );
    }

    public boolean existe(String nome) {
        return tabela.containsKey(nome);
    }

    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabela.get(nome);
    }

    public TipoLA verificarTipo(String nome) {

        EntradaTabelaDeSimbolos entrada =
                tabela.get(nome);

        if (entrada == null) {
            return TipoLA.TIPO_INDEFINIDO;
        }

        return entrada.tipo;
    }
}