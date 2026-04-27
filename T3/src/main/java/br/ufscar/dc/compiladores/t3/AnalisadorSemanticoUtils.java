package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária com métodos de verificação de tipos e coleta de erros
 * semânticos.
 *
 * Centraliza a lógica de inferência de tipos para expressões aritméticas,
 * lógicas e relacionais, além de registrar mensagens de erro no formato
 * exigido pelo professor.
 */
public class AnalisadorSemanticoUtils {

    // Lista acumulada de erros semânticos encontrados durante a análise
    public static List<String> errosSemanticos = new ArrayList<>();

    /**
     * Registra um erro semântico indicando o número de linha do token.
     * Formato: "Linha X: mensagem"
     */
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    // ----------------------------------------------------------------
    // Métodos de verificação de tipo para cada nível de expressão
    // ----------------------------------------------------------------

    /**
     * Determina o tipo de uma expressão lógica (OR de termos lógicos).
     * Resulta em LOGICO se todos os termos são LOGICO, TIPO_INDEFINIDO c.c.
     */
    public static TipoLA verificarTipo(Escopos escopos, JanderParser.ExpressaoContext ctx) {
        TipoLA ret = null;
        for (var termoCtx : ctx.termo_logico()) {
            TipoLA aux = verificarTipoTermoLogico(escopos, termoCtx);
            if (ret == null) {
                ret = aux;
            } else if (!tiposCompativeis(ret, aux)) {
                ret = TipoLA.TIPO_INDEFINIDO;
            }
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de um termo lógico (AND de fatores lógicos).
     */
    public static TipoLA verificarTipoTermoLogico(Escopos escopos,
            JanderParser.Termo_logicoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator_logico()) {
            TipoLA aux = verificarTipoFatorLogico(escopos, fatorCtx);
            if (ret == null) {
                ret = aux;
            } else if (!tiposCompativeis(ret, aux)) {
                ret = TipoLA.TIPO_INDEFINIDO;
            }
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de um fator lógico (NOT opcional + parcela lógica).
     */
    public static TipoLA verificarTipoFatorLogico(Escopos escopos,
            JanderParser.Fator_logicoContext ctx) {
        return verificarTipoParcela(escopos, ctx.parcela_logica());
    }

    /**
     * Determina o tipo de uma parcela lógica.
     * Pode ser: verdadeiro/falso (LOGICO) ou expressão relacional.
     */
    public static TipoLA verificarTipoParcela(Escopos escopos,
            JanderParser.Parcela_logicaContext ctx) {
        if (ctx.VERDADEIRO() != null || ctx.FALSO() != null) {
            return TipoLA.LOGICO;
        }
        return verificarTipoRelacional(escopos, ctx.exp_relacional());
    }

    /**
     * Determina o tipo de uma expressão relacional.
     * Se houver operador relacional, o resultado é LOGICO.
     * Caso contrário, repassa o tipo da expressão aritmética.
     */
    public static TipoLA verificarTipoRelacional(Escopos escopos,
            JanderParser.Exp_relacionalContext ctx) {
        TipoLA tipoEsq = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            // Há operador relacional — ambos os lados devem ser compatíveis
            TipoLA tipoDir = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(1));
            if (!tiposCompativeis(tipoEsq, tipoDir)) {
                return TipoLA.TIPO_INDEFINIDO;
            }
            return TipoLA.LOGICO;
        }
        return tipoEsq;
    }

    /**
     * Determina o tipo de uma expressão aritmética (soma/subtração de termos).
     * Inteiro op Inteiro → Inteiro
     * Real op (Real|Inteiro) → Real
     * Qualquer coisa com LITERAL ou LOGICO → TIPO_INDEFINIDO
     */
    public static TipoLA verificarTipoAritmetica(Escopos escopos,
            JanderParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = null;
        for (var termoCtx : ctx.termo()) {
            TipoLA aux = verificarTipoTermo(escopos, termoCtx);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de um termo (multiplicação/divisão de fatores).
     */
    public static TipoLA verificarTipoTermo(Escopos escopos,
            JanderParser.TermoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator()) {
            TipoLA aux = verificarTipoFator(escopos, fatorCtx);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de um fator (parcelas com módulo).
     */
    public static TipoLA verificarTipoFator(Escopos escopos,
            JanderParser.FatorContext ctx) {
        TipoLA ret = null;
        for (var parcelaCtx : ctx.parcela()) {
            TipoLA aux = verificarTipoParcela(escopos, parcelaCtx);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de uma parcela aritmética.
     * Delega para parcela_unario ou parcela_nao_unario.
     */
    public static TipoLA verificarTipoParcela(Escopos escopos,
            JanderParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            return verificarTipoParcelaUnario(escopos, ctx.parcela_unario());
        } else {
            return verificarTipoParcelaNaoUnario(escopos, ctx.parcela_nao_unario());
        }
    }

    /**
     * Determina o tipo de uma parcela unária:
     * - identificador (variável, campo de registro)
     * - chamada de função
     * - NUM_INT, NUM_REAL
     * - expressão entre parênteses
     */
    public static TipoLA verificarTipoParcelaUnario(Escopos escopos,
            JanderParser.Parcela_unarioContext ctx) {

        if (ctx.NUM_INT() != null) {
            return TipoLA.INTEIRO;
        }
        if (ctx.NUM_REAL() != null) {
            return TipoLA.REAL;
        }
        if (ctx.ABREPAR() != null) {
            // Expressão entre parênteses
            return verificarTipo(escopos, ctx.expressao(0));
        }
        if (ctx.IDENT() != null && ctx.ABREPAR() == null) {
            // Pode ser identificador simples (sem parênteses = não é chamada)
            // Mas a regra tem IDENT ABREPAR para chamadas; verificamos via identificador()
        }
        if (ctx.identificador() != null) {
            // Variável ou campo de registro
            return verificarTipoIdentificador(escopos, ctx.identificador());
        }
        if (ctx.IDENT() != null) {
            // Chamada de função: IDENT ( expressoes )
            String nomeFuncao = ctx.IDENT().getText();
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escopos.verificar(nomeFuncao);
            if (entrada == null) {
                // O erro de não-declarado será reportado em outro visitor
                return TipoLA.TIPO_INDEFINIDO;
            }
            return entrada.tipo;
        }
        return TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Determina o tipo de uma parcela não-unária:
     * - &identificador → ponteiro para o tipo do identificador
     * - CADEIA → LITERAL
     */
    public static TipoLA verificarTipoParcelaNaoUnario(Escopos escopos,
            JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) {
            return TipoLA.LITERAL;
        }
        if (ctx.ENDERECO_OP() != null) {
            TipoLA tipoBase = verificarTipoIdentificador(escopos, ctx.identificador());
            return converterParaPonteiro(tipoBase);
        }
        return TipoLA.TIPO_INDEFINIDO;
    }

    /**
     * Resolve o tipo de um identificador (variável simples ou campo de registro).
     * Navega pela cadeia de campos (a.b.c) buscando nos escopos.
     */
    public static TipoLA verificarTipoIdentificador(Escopos escopos,
            JanderParser.IdentificadorContext ctx) {
        // O primeiro IDENT é o nome da variável base
        String nomeBase = ctx.IDENT(0).getText();
        TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escopos.verificar(nomeBase);
        if (entrada == null) {
            return TipoLA.TIPO_INDEFINIDO;
        }
        // Campos de registro são tratados de forma simplificada:
        // o tipo final é o tipo da entrada base
        return entrada.tipo;
    }

    // ----------------------------------------------------------------
    // Helpers de compatibilidade de tipos
    // ----------------------------------------------------------------

    /**
     * Verifica se dois tipos são compatíveis para uma atribuição.
     * Regras conforme especificação do T3:
     *   ponteiro ← endereço (mesmo tipo base)
     *   (real | inteiro) ← (real | inteiro)
     *   literal ← literal
     *   logico ← logico
     *   registro ← registro (mesmo nome de tipo)
     */
    public static boolean tiposCompativeisAtribuicao(
            TabelaDeSimbolos.EntradaTabelaDeSimbolos destino,
            TipoLA tipoFonte) {

        if (destino.tipo == TipoLA.TIPO_INDEFINIDO || tipoFonte == TipoLA.TIPO_INDEFINIDO) {
            return false;
        }
        // Numéricos: inteiro e real são compatíveis entre si
        if (ehNumerico(destino.tipo) && ehNumerico(tipoFonte)) {
            return true;
        }
        // Ponteiro recebe endereço: destino ponteiro, fonte deve ser ponteiro compatível
        if (ehPonteiro(destino.tipo) && ehPonteiro(tipoFonte)) {
            return tipoBaseDosPonteiros(destino.tipo) == tipoBaseDosPonteiros(tipoFonte);
        }
        // Registro: mesmo nome de tipo
        if (destino.tipo == TipoLA.REGISTRO && tipoFonte == TipoLA.REGISTRO) {
            return true; // verificação de nome é feita no visitor
        }
        // Demais: devem ser exatamente iguais
        return destino.tipo == tipoFonte;
    }

    /**
     * Verifica compatibilidade genérica entre dois tipos (para expressões).
     * Inteiro e Real são compatíveis entre si; outros devem ser iguais.
     */
    public static boolean tiposCompativeis(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.TIPO_INDEFINIDO || t2 == TipoLA.TIPO_INDEFINIDO) {
            return false;
        }
        if (ehNumerico(t1) && ehNumerico(t2)) {
            return true;
        }
        return t1 == t2;
    }

    /**
     * Combina dois tipos numéricos em operação aritmética.
     * int op int → int; qualquer real envolvido → real; não-numérico → indefinido.
     */
    public static TipoLA combinarTiposNumericos(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.TIPO_INDEFINIDO || t2 == TipoLA.TIPO_INDEFINIDO) {
            return TipoLA.TIPO_INDEFINIDO;
        }
        if (!ehNumerico(t1) || !ehNumerico(t2)) {
            return TipoLA.TIPO_INDEFINIDO;
        }
        if (t1 == TipoLA.REAL || t2 == TipoLA.REAL) {
            return TipoLA.REAL;
        }
        return TipoLA.INTEIRO;
    }

    /**
     * Converte um tipo básico para seu equivalente ponteiro.
     */
    public static TipoLA converterParaPonteiro(TipoLA base) {
        switch (base) {
            case INTEIRO:  return TipoLA.PONTEIRO_INTEIRO;
            case REAL:     return TipoLA.PONTEIRO_REAL;
            case LITERAL:  return TipoLA.PONTEIRO_LITERAL;
            case LOGICO:   return TipoLA.PONTEIRO_LOGICO;
            case REGISTRO: return TipoLA.PONTEIRO_REGISTRO;
            default:       return TipoLA.TIPO_INDEFINIDO;
        }
    }

    /**
     * Informa se o tipo é numérico (inteiro ou real).
     */
    public static boolean ehNumerico(TipoLA t) {
        return t == TipoLA.INTEIRO || t == TipoLA.REAL;
    }

    /**
     * Informa se o tipo é ponteiro.
     */
    public static boolean ehPonteiro(TipoLA t) {
        return t == TipoLA.PONTEIRO_INTEIRO || t == TipoLA.PONTEIRO_REAL
                || t == TipoLA.PONTEIRO_LITERAL || t == TipoLA.PONTEIRO_LOGICO
                || t == TipoLA.PONTEIRO_REGISTRO;
    }

    /**
     * Retorna o tipo base de um ponteiro.
     */
    public static TipoLA tipoBaseDosPonteiros(TipoLA t) {
        switch (t) {
            case PONTEIRO_INTEIRO:  return TipoLA.INTEIRO;
            case PONTEIRO_REAL:     return TipoLA.REAL;
            case PONTEIRO_LITERAL:  return TipoLA.LITERAL;
            case PONTEIRO_LOGICO:   return TipoLA.LOGICO;
            case PONTEIRO_REGISTRO: return TipoLA.REGISTRO;
            default:                return TipoLA.TIPO_INDEFINIDO;
        }
    }

    /**
     * Converte string do tipo (como aparece na gramática) para enum TipoLA.
     */
    public static TipoLA stringParaTipo(String s, boolean ehPonteiro) {
        TipoLA base;
        switch (s) {
            case "inteiro": base = TipoLA.INTEIRO;  break;
            case "real":    base = TipoLA.REAL;     break;
            case "literal": base = TipoLA.LITERAL;  break;
            case "logico":  base = TipoLA.LOGICO;   break;
            default:        base = TipoLA.REGISTRO; break; // nome de tipo definido
        }
        return ehPonteiro ? converterParaPonteiro(base) : base;
    }
}