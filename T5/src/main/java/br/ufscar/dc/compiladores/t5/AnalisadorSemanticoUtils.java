package br.ufscar.dc.compiladores.t5;

import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalisadorSemanticoUtils {

    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionarErroSemantico(Token t, String mensagem) {
        errosSemanticos.add(String.format("Linha %d: %s", t.getLine(), mensagem));
    }

    public static TipoLA verificarTipo(Escopos escopos, JanderParser.ExpressaoContext ctx) {
        TipoLA ret = null;
        for (var termo : ctx.termo_logico()) {
            TipoLA aux = verificarTipoTermoLogico(escopos, termo);
            if (ret == null) {
                ret = aux;
            } else if (!tiposCompativeis(ret, aux)) {
                ret = TipoLA.TIPO_INDEFINIDO;
            }
        }
        return ret;
    }

    public static TipoLA verificarTipoTermoLogico(Escopos escopos, JanderParser.Termo_logicoContext ctx) {
        TipoLA ret = null;
        for (var fator : ctx.fator_logico()) {
            TipoLA aux = verificarTipoFatorLogico(escopos, fator);
            if (ret == null) {
                ret = aux;
            } else if (!tiposCompativeis(ret, aux)) {
                ret = TipoLA.TIPO_INDEFINIDO;
            }
        }
        return ret;
    }

    public static TipoLA verificarTipoFatorLogico(Escopos escopos, JanderParser.Fator_logicoContext ctx) {
        return verificarTipoParcelaLogica(escopos, ctx.parcela_logica());
    }

    public static TipoLA verificarTipoParcelaLogica(Escopos escopos, JanderParser.Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            return verificarTipoRelacional(escopos, ctx.exp_relacional());
        }
        return TipoLA.LOGICO;
    }

    public static TipoLA verificarTipoRelacional(Escopos escopos, JanderParser.Exp_relacionalContext ctx) {
        TipoLA ret = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            TipoLA outro = verificarTipoAritmetica(escopos, ctx.exp_aritmetica(1));
            if (!tiposCompativeis(ret, outro)) {
                return TipoLA.TIPO_INDEFINIDO;
            }
            return TipoLA.LOGICO;
        }
        return ret;
    }

    public static TipoLA verificarTipoAritmetica(Escopos escopos, JanderParser.Exp_aritmeticaContext ctx) {
        TipoLA ret = null;
        for (var termo : ctx.termo()) {
            TipoLA aux = verificarTipoTermo(escopos, termo);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return ret;
    }

    public static TipoLA verificarTipoTermo(Escopos escopos, JanderParser.TermoContext ctx) {
        TipoLA ret = null;
        for (var fator : ctx.fator()) {
            TipoLA aux = verificarTipoFator(escopos, fator);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return ret;
    }

    public static TipoLA verificarTipoFator(Escopos escopos, JanderParser.FatorContext ctx) {
        TipoLA ret = null;
        for (var parcela : ctx.parcela()) {
            TipoLA aux = verificarTipoParcela(escopos, parcela);
            if (ret == null) {
                ret = aux;
            } else {
                ret = combinarTiposNumericos(ret, aux);
            }
        }
        return ret;
    }

    public static TipoLA verificarTipoParcela(Escopos escopos, JanderParser.ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            return verificarTipoParcelaUnario(escopos, ctx.parcela_unario());
        }
        return verificarTipoParcelaNaoUnario(escopos, ctx.parcela_nao_unario());
    }

    public static TipoLA verificarTipoParcelaUnario(Escopos escopos, JanderParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            return TipoLA.INTEIRO;
        }
        if (ctx.NUM_REAL() != null) {
            return TipoLA.REAL;
        }
        // Chamada de função: IDENT ABREPAR args FECHAPAR — deve vir ANTES de identificador
        // pois ambos começam com IDENT e o ANTLR pode capturar como identificador
        if (ctx.IDENT() != null && ctx.ABREPAR() != null) {
            String nome = ctx.IDENT().getText();
            var entrada = escopos.verificar(nome);

            if (entrada == null) {
                adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nome + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }

            List<JanderParser.ExpressaoContext> argumentos = ctx.expressao();

            if (argumentos.size() != entrada.tiposParametros.size()) {
                adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
            } else {
                for (int i = 0; i < argumentos.size(); i++) {
                    TipoLA tipoArg = verificarTipo(escopos, argumentos.get(i));
                    TipoLA tipoParam = entrada.tiposParametros.get(i);
                    if (!tiposCompativeisParametro(tipoParam, tipoArg)) {
                        adicionarErroSemantico(ctx.IDENT().getSymbol(), "incompatibilidade de parametros na chamada de " + nome);
                        break;
                    }
                }
            }

            return entrada.tipo;
        }

        // Identificador simples (variável, campo de registro, ponteiro)
        if (ctx.identificador() != null) {
            return verificarTipoIdentificador(escopos, ctx.identificador(), ctx.PONTEIRO_OP() != null);
        }

        if (ctx.expressao() != null && !ctx.expressao().isEmpty()) {
            return verificarTipo(escopos, ctx.expressao(0));
        }

        return TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoParcelaNaoUnario(Escopos escopos, JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) {
            return TipoLA.LITERAL;
        }
        if (ctx.identificador() != null) {
            return verificarTipoIdentificador(escopos, ctx.identificador(), false);
        }
        return TipoLA.TIPO_INDEFINIDO;
    }

    public static TipoLA verificarTipoIdentificador(Escopos escopos, JanderParser.IdentificadorContext ctx, boolean desreferenciar) {
        String nomeBase = ctx.IDENT(0).getText();
        var entrada = escopos.verificar(nomeBase);

        if (entrada == null) {
            adicionarErroSemantico(ctx.IDENT(0).getSymbol(), "identificador " + ctx.getText() + " nao declarado");
            return TipoLA.TIPO_INDEFINIDO;
        }

        TipoLA tipoAtual = entrada.tipo;
        Map<String, TipoLA> camposAtuais = entrada.camposRegistro;

        for (int i = 1; i < ctx.IDENT().size(); i++) {
            String nomeCampo = ctx.IDENT(i).getText();
            if (camposAtuais == null || !camposAtuais.containsKey(nomeCampo)) {
                adicionarErroSemantico(ctx.IDENT(i).getSymbol(), "identificador " + ctx.getText() + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }
            tipoAtual = camposAtuais.get(nomeCampo);
            
            if (tipoAtual == TipoLA.REGISTRO && entrada.nomeRegistro != null) {
                var entradaTipoMestre = escopos.verificar(entrada.nomeRegistro);
                camposAtuais = (entradaTipoMestre != null) ? entradaTipoMestre.camposRegistro : null;
            } else {
                camposAtuais = null;
            }
        }

        if (desreferenciar) {
            return desreferenciarPonteiro(tipoAtual);
        }

        return tipoAtual;
    }

    public static boolean tiposCompativeisParametro(TipoLA esperado, TipoLA recebido) {
        return esperado == recebido;
    }

    public static boolean tiposCompativeis(TipoLA t1, TipoLA t2) {
        if (t1 == t2) return true;
        return (t1 == TipoLA.REAL && t2 == TipoLA.INTEIRO) || (t1 == TipoLA.INTEIRO && t2 == TipoLA.REAL);
    }

    public static boolean tiposCompativeisAtribuicao(TipoLA destino, String nomeDestino, TipoLA origem, String nomeOrigem) {
        //melhor análise das atribuições compatíveis, deixando menos geral e proporcionando uma melhor análise
        if (destino == TipoLA.TIPO_INDEFINIDO || origem == TipoLA.TIPO_INDEFINIDO) {
        return true; 
        }
        if (destino == origem) {
            return true;
        }
        return destino == TipoLA.REAL && origem == TipoLA.INTEIRO;
    }

    public static TipoLA combinarTiposNumericos(TipoLA t1, TipoLA t2) {
        if (!ehNumerico(t1) || !ehNumerico(t2)) {
            return TipoLA.TIPO_INDEFINIDO;
        }
        if (t1 == TipoLA.REAL || t2 == TipoLA.REAL) {
            return TipoLA.REAL;
        }
        return TipoLA.INTEIRO;
    }

    public static boolean ehNumerico(TipoLA tipo) {
        return tipo == TipoLA.INTEIRO || tipo == TipoLA.REAL;
    }

    public static TipoLA stringParaTipo(String texto, boolean ponteiro) {
        TipoLA tipo;
        switch (texto) {
            case "inteiro": tipo = TipoLA.INTEIRO; break;
            case "real": tipo = TipoLA.REAL; break;
            case "literal": tipo = TipoLA.LITERAL; break;
            case "logico": tipo = TipoLA.LOGICO; break;
            default: return TipoLA.REGISTRO;
        }
        if (!ponteiro) return tipo;
        return referenciarComoPonteiro(tipo);
    }

    public static TipoLA referenciarComoPonteiro(TipoLA tipo) {
        switch (tipo) {
            case INTEIRO: return TipoLA.PONTEIRO_INTEIRO;
            case REAL: return TipoLA.PONTEIRO_REAL;
            case LITERAL: return TipoLA.PONTEIRO_LITERAL;
            case LOGICO: return TipoLA.PONTEIRO_LOGICO;
            case REGISTRO: return TipoLA.PONTEIRO_REGISTRO;
            default: return TipoLA.TIPO_INDEFINIDO;
        }
    }

    public static TipoLA desreferenciarPonteiro(TipoLA tipo) {
        switch (tipo) {
            case PONTEIRO_INTEIRO: return TipoLA.INTEIRO;
            case PONTEIRO_REAL: return TipoLA.REAL;
            case PONTEIRO_LITERAL: return TipoLA.LITERAL;
            case PONTEIRO_LOGICO: return TipoLA.LOGICO;
            case PONTEIRO_REGISTRO: return TipoLA.REGISTRO;
            default: return tipo;
        }
    }
}