package br.ufscar.dc.compiladores.t4;

import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.TipoLA;
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

    public static TipoLA verificarTipoTermoLogico(Escopos escopos,
            JanderParser.Termo_logicoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator_logico()) {
            TipoLA aux = verificarTipoFatorLogico(escopos, fatorCtx);
            if (ret == null) ret = aux;
            else if (!tiposCompativeis(ret, aux)) ret = TipoLA.TIPO_INDEFINIDO;
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoFatorLogico(Escopos escopos,
            JanderParser.Fator_logicoContext ctx) {
        return verificarTipoParcelaLogica(escopos, ctx.parcela_logica());
    }

    public static TipoLA verificarTipoParcelaLogica(Escopos escopos,
            JanderParser.Parcela_logicaContext ctx) {
        if (ctx.VERDADEIRO() != null || ctx.FALSO() != null) {
            return TipoLA.LOGICO;
        }
        return verificarTipoRelacional(escopos, ctx.exp_relacional());
    }

    
    public static TipoLA verificarTipoRelacional(Escopos escopos,
            JanderParser.Exp_relacionalContext ctx) {
        TipoLA tipoEsq = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            TipoLA tipoDir = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(1));
            if (!tiposCompativeis(tipoEsq, tipoDir)) return TipoLA.TIPO_INDEFINIDO;
            return TipoLA.LOGICO;
        }
        return tipoEsq;
    }

    public static TipoLA verificarTipoAritmetica(Escopos escopos,
            JanderParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = null;
        for (var termoCtx : ctx.termo()) {
            TipoLA aux = verificarTipoTermo(escopos, termoCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoTermo(Escopos escopos,
            JanderParser.TermoContext ctx) {
        TipoLA ret = null;
        for (var fatorCtx : ctx.fator()) {
            TipoLA aux = verificarTipoFator(escopos, fatorCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoFator(Escopos escopos,
            JanderParser.FatorContext ctx) {
        TipoLA ret = null;
        for (var parcelaCtx : ctx.parcela()) {
            TipoLA aux = verificarTipoParcela(escopos, parcelaCtx);
            if (ret == null) ret = aux;
            else ret = combinarTiposNumericos(ret, aux);
        }
        return (ret != null) ? ret : TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoParcela(Escopos escopos,
            JanderParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null)
            return verificarTipoParcelaUnario(escopos, ctx.parcela_unario());
        return verificarTipoParcelaNaoUnario(escopos, ctx.parcela_nao_unario());
    }

  
    public static TipoLA verificarTipoParcelaUnario(Escopos escopos,
            JanderParser.Parcela_unarioContext ctx) {

        if (ctx.NUM_INT() != null) return TipoLA.INTEIRO;
        if (ctx.NUM_REAL() != null) return TipoLA.REAL;

        if (ctx.ABREPAR() != null) return verificarTipo(escopos, ctx.expressao(0));

        if (ctx.IDENT() != null && ctx.ABREPAR() != null) {
            String nome = ctx.IDENT().getText();
            var entrada = escopos.verificar(nome);
            if (entrada == null) {
                adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + nome + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }
            return entrada.tipo;
        }

        if (ctx.identificador() != null) {
            String nome = ctx.identificador().IDENT(0).getText();
            var entrada = escopos.verificar(nome);
            if (entrada == null) {
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "identificador " + nome + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }

            
            TipoLA tipo = entrada.tipo;
            boolean temPonteiro = ctx.PONTEIRO_OP() != null
                    || (ctx.identificador().PONTEIRO_OP() != null);
            if (temPonteiro) {
                tipo = desreferenciarPonteiro(tipo);
            }
            return tipo;
        }

        if (ctx.IDENT() != null) {
            String nome = ctx.IDENT().getText();
            var entrada = escopos.verificar(nome);
            if (entrada == null) {
                adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + nome + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }
            return entrada.tipo;
        }

        return TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoParcelaNaoUnario(Escopos escopos,
            JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) return TipoLA.LITERAL;

        if (ctx.ENDERECO_OP() != null && ctx.identificador() != null) {
            String nome = ctx.identificador().IDENT(0).getText();
            var entrada = escopos.verificar(nome);
            if (entrada == null) {
                adicionarErroSemantico(ctx.identificador().IDENT(0).getSymbol(),
                        "identificador " + nome + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }
            return referenciarComoPointer(entrada.tipo);
        }

        return TipoLA.TIPO_INDEFINIDO;
    }

   
    public static boolean tiposCompativeis(TipoLA t1, TipoLA t2) {
        if (t1 == t2) return true;

        if ((t1 == TipoLA.REAL && t2 == TipoLA.INTEIRO)
                || (t1 == TipoLA.INTEIRO && t2 == TipoLA.REAL)) return true;

        if (t1 == TipoLA.PONTEIRO_INTEIRO && t2 == TipoLA.INTEIRO) return true;
        if (t1 == TipoLA.PONTEIRO_REAL    && t2 == TipoLA.REAL)    return true;
        if (t1 == TipoLA.PONTEIRO_LITERAL && t2 == TipoLA.LITERAL) return true;
        if (t1 == TipoLA.PONTEIRO_LOGICO  && t2 == TipoLA.LOGICO)  return true;

        return false;
    }

    public static boolean tiposCompativeisAtribuicao(
            TipoLA tipoVar, String nomeRegVar,
            TipoLA tipoExpr, String nomeRegExpr) {

        if (tipoVar == TipoLA.PONTEIRO_INTEIRO && tipoExpr == TipoLA.PONTEIRO_INTEIRO) return true;
        if (tipoVar == TipoLA.PONTEIRO_REAL    && tipoExpr == TipoLA.PONTEIRO_REAL)    return true;
        if (tipoVar == TipoLA.PONTEIRO_LITERAL && tipoExpr == TipoLA.PONTEIRO_LITERAL) return true;
        if (tipoVar == TipoLA.PONTEIRO_LOGICO  && tipoExpr == TipoLA.PONTEIRO_LOGICO)  return true;

        if (tipoVar == TipoLA.REGISTRO && tipoExpr == TipoLA.REGISTRO) {
            if (nomeRegVar == null || nomeRegExpr == null) return false;
            return nomeRegVar.equals(nomeRegExpr);
        }

        return tiposCompativeis(tipoVar, tipoExpr);
    }

    public static boolean tiposCompativeisParametro(
            TipoLA tipoParam, String nomeRegParam,
            TipoLA tipoArg, String nomeRegArg) {
        return tiposCompativeisAtribuicao(tipoParam, nomeRegParam, tipoArg, nomeRegArg);
    }

    public static TipoLA combinarTiposNumericos(TipoLA t1, TipoLA t2) {
        if (t1 == TipoLA.LITERAL && t2 == TipoLA.LITERAL) return TipoLA.LITERAL;
        if (!ehNumerico(t1) || !ehNumerico(t2)) return TipoLA.TIPO_INDEFINIDO;
        if (t1 == TipoLA.REAL || t2 == TipoLA.REAL) return TipoLA.REAL;
        return TipoLA.INTEIRO;
    }

    public static boolean ehNumerico(TipoLA t) {
        return t == TipoLA.INTEIRO || t == TipoLA.REAL;
    }

    public static TipoLA stringParaTipo(String s, boolean ehPonteiro) {
        TipoLA base;
        switch (s) {
            case "inteiro": base = TipoLA.INTEIRO; break;
            case "real":    base = TipoLA.REAL;    break;
            case "literal": base = TipoLA.LITERAL; break;
            case "logico":  base = TipoLA.LOGICO;  break;
            default:        return TipoLA.TIPO_INDEFINIDO;
        }
        if (!ehPonteiro) return base;

        switch (base) {
            case INTEIRO: return TipoLA.PONTEIRO_INTEIRO;
            case REAL:    return TipoLA.PONTEIRO_REAL;
            case LITERAL: return TipoLA.PONTEIRO_LITERAL;
            case LOGICO:  return TipoLA.PONTEIRO_LOGICO;
            default:      return TipoLA.TIPO_INDEFINIDO;
        }
    }

    
    public static TipoLA referenciarComoPointer(TipoLA base) {
        switch (base) {
            case INTEIRO:  return TipoLA.PONTEIRO_INTEIRO;
            case REAL:     return TipoLA.PONTEIRO_REAL;
            case LITERAL:  return TipoLA.PONTEIRO_LITERAL;
            case LOGICO:   return TipoLA.PONTEIRO_LOGICO;
            case REGISTRO: return TipoLA.PONTEIRO_REGISTRO;
            default:       return TipoLA.TIPO_INDEFINIDO;
        }
    }

    
    public static TipoLA desreferenciarPonteiro(TipoLA ponteiro) {
        switch (ponteiro) {
            case PONTEIRO_INTEIRO:  return TipoLA.INTEIRO;
            case PONTEIRO_REAL:     return TipoLA.REAL;
            case PONTEIRO_LITERAL:  return TipoLA.LITERAL;
            case PONTEIRO_LOGICO:   return TipoLA.LOGICO;
            case PONTEIRO_REGISTRO: return TipoLA.REGISTRO;
            default:                return ponteiro; 
        }
    }
}