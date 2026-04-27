package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;
import java.util.List;

public class AnalisadorSemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    public static TipoLA verificarTipo(Escopos escopos, JanderParser.ExpressaoContext ctx) {
        TipoLA ret = null;
        for (var termoCtx : ctx.termo_logico()) {
            TipoLA aux = verificarTipoTermoLogico(escopos, termoCtx);
            if (ret == null) ret = aux;
            else if (!tiposCompativeis(ret, aux)) ret = TipoLA.TIPO_INDEFINIDO;
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoTermoLogico(Escopos escopos, JanderParser.Termo_logicoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator_logico()) {
            TipoLA aux = verificarTipoFatorLogico(escopos, fatorCtx);
            if (ret == null) ret = aux;
            else if (!tiposCompativeis(ret, aux)) ret = TipoLA.TIPO_INDEFINIDO;
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoFatorLogico(Escopos escopos, JanderParser.Fator_logicoContext ctx) {
        return verificarTipoParcelaLogica(escopos, ctx.parcela_logica());
    }

    public static TipoLA verificarTipoParcelaLogica(Escopos escopos, JanderParser.Parcela_logicaContext ctx) {
        if (ctx.VERDADEIRO() != null || ctx.FALSO() != null) {
            return TipoLA.LOGICO;
        }
        return verificarTipoRelacional(escopos, ctx.exp_relacional());
    }

    public static TipoLA verificarTipoRelacional(Escopos escopos, JanderParser.Exp_relacionalContext ctx) {
        TipoLA tipoEsq = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            TipoLA tipoDir = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(1));
            if (!tiposCompativeis(tipoEsq, tipoDir)) return TipoLA.TIPO_INDEFINIDO;
            return TipoLA.LOGICO;
        }
        return tipoEsq;
    }

    public static TipoLA verificarTipoAritmetica(Escopos escopos, JanderParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = null;
        for (var termoCtx : ctx.termo()) {
            TipoLA aux = verificarTipoTermo(escopos, termoCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoTermo(Escopos escopos, JanderParser.TermoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator()) {
            TipoLA aux = verificarTipoFator(escopos, fatorCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoFator(Escopos escopos, JanderParser.FatorContext ctx) {
        TipoLA ret = null;
        for (var parcelaCtx : ctx.parcela()) {
            TipoLA aux = verificarTipoParcela(escopos, parcelaCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoParcela(Escopos escopos, JanderParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) return verificarTipoParcelaUnario(escopos, ctx.parcela_unario());
        return verificarTipoParcelaNaoUnario(escopos, ctx.parcela_nao_unario());
    }

    public static TipoLA verificarTipoParcelaUnario(Escopos escopos, JanderParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;
        if (ctx.ABREPAR() != null) return verificarTipo(escopos, ctx.expressao(0));
        if (ctx.identificador() != null) {
            String nome = ctx.identificador().IDENT(0).getText();
            var entrada = escopos.verificar(nome);
            return (entrada != null) ? entrada.tipo : TipoLA.TIPO_INDEFINIDO;
        }
        if (ctx.IDENT() != null) {
            var entrada = escopos.verificar(ctx.IDENT().getText());
            return (entrada != null) ? entrada.tipo : TipoLA.TIPO_INDEFINIDO;
        }
        return TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoParcelaNaoUnario(Escopos escopos, JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;
        return TipoLA.TIPO_INDEFINIDO;
    }

    public static boolean tiposCompativeis(TipoLA t1, TipoLA t2) {
        if (t1 == t2) return true;
        if ((t1 == TipoLA.REAL && t2 == TipoLA.INTEIRO) || (t1 == TipoLA.INTEIRO && t2 == TipoLA.REAL)) return true;
        return false;
    }

    public static TipoLA combinarTiposNumericos(TipoLA t1, TipoLA t2) {
        if (!ehNumerico(t1) || !ehNumerico(t2)) return TipoLA.TIPO_INDEFINIDO;
        if (t1 == TipoLA.REAL || t2 == TipoLA.REAL) return TipoLA.REAL;
        return TipoLA.INTEIRO;
    }

    public static boolean ehNumerico(TipoLA t) {
        return t == TipoLA.INTEIRO || t == TipoLA.REAL;
    }

    public static TipoLA stringParaTipo(String s, boolean ehPonteiro) {
        TipoLA tipo = TipoLA.TIPO_INDEFINIDO;
        switch (s) {
            case "inteiro": tipo = TipoLA.INTEIRO; break;
            case "real": tipo = TipoLA.REAL; break;
            case "literal": tipo = TipoLA.LITERAL; break;
            case "logico": tipo = TipoLA.LOGICO; break;
        }
        return tipo;
    }
}