package br.ufscar.dc.compiladores.t5;

import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;
import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.CategoriaSimbolos;

public class JanderGeradorC extends JanderBaseVisitor<Void> {

    private final StringBuilder saida;
    private Escopos escopos; // Tabela de símbolos local do gerador

    public JanderGeradorC() {
        this.saida = new StringBuilder();
    }

    public String getCodigoGerado() {
        return saida.toString();
    }

    // --- PROGRAMA PRINCIPAL ---
    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos(); // Inicializa o escopo global do gerador

        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n");
        saida.append("#include <stdbool.h>\n\n");

        if (ctx.declaracoes() != null) {
            visitDeclaracoes(ctx.declaracoes());
        }

        saida.append("\n");
        saida.append("int main() {\n");
        if (ctx.corpo() != null) {
            visitCorpo(ctx.corpo());
        }
        saida.append("    return 0;\n");
        saida.append("}\n");

        return null;
    }

    @Override
    public Void visitCorpo(JanderParser.CorpoContext ctx) {
        for (var declLocal : ctx.declaracao_local()) {
            visitDeclaracao_local(declLocal);
        }
        for (var cmd : ctx.cmd()) {
            visit(cmd);
        }
        return null;
    }

    // --- DECLARAÇÕES LOCAIS ---
    @Override
    public Void visitDeclaracao_local(JanderParser.Declaracao_localContext ctx) {
        if (ctx.DECLARE() != null && ctx.variavel() != null) {
            String strTipoJander = ctx.variavel().tipo().getText();
            // Converte a string do tipo para o enum TipoLA
            TipoLA tipoEnum = AnalisadorSemanticoUtils.stringParaTipo(strTipoJander, false);
            String strTipoC = tokenTipoParaC(strTipoJander);
            
            for (int i = 0; i < ctx.variavel().identificador().size(); i++) {
                var ident = ctx.variavel().identificador(i);
                String nomeVar = ident.getText();
                
                // Salva a variável na tabela do gerador para podermos consultar depois
                escopos.obterEscopoAtual().adicionar(nomeVar, tipoEnum, CategoriaSimbolos.VARIAVEL);
                
                saida.append("\t").append(strTipoC).append(" ");
                visitIdentificador(ident);
                
                if (tipoEnum == TipoLA.LITERAL) {
                    saida.append("[80]"); 
                }
                saida.append(";\n");
            }
        } 
        return null;
    }

    // --- COMANDOS (LEIA / ESCREVA) ---
    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            // Busca o tipo real na tabela populada do gerador
            TipoLA tipo = AnalisadorSemanticoUtils.verificarTipoIdentificador(escopos, ident, false);
            String fmt = obterFormatScanfPrintf(tipo);
            
            saida.append("\tscanf(\"").append(fmt).append("\", ");
            if (tipo == TipoLA.LITERAL) {
                saida.append(ident.getText()); 
            } else {
                saida.append("&").append(ident.getText());
            }
            saida.append(");\n");
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            // Avalia o tipo resultante da expressão
            TipoLA tipo = AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
            String fmt = obterFormatScanfPrintf(tipo);
            
            saida.append("\tprintf(\"").append(fmt).append("\", ");
            visitExpressao(expr);
            saida.append(");\n");
        }
        return null;
    }

    // --- EXPRESSÕES ---
    @Override
    public Void visitExpressao(JanderParser.ExpressaoContext ctx) {
        for (int i = 0; i < ctx.termo_logico().size(); i++) {
            if (i > 0) saida.append(" || ");
            visitTermo_logico(ctx.termo_logico(i));
        }
        return null;
    }

    @Override
    public Void visitTermo_logico(JanderParser.Termo_logicoContext ctx) {
        for (int i = 0; i < ctx.fator_logico().size(); i++) {
            if (i > 0) saida.append(" && ");
            visitFator_logico(ctx.fator_logico(i));
        }
        return null;
    }

    @Override
    public Void visitFator_logico(JanderParser.Fator_logicoContext ctx) {
        if (ctx.OP_BOOL_NAO() != null) {
            saida.append("!");
        }
        visitParcela_logica(ctx.parcela_logica());
        return null;
    }

    @Override
    public Void visitParcela_logica(JanderParser.Parcela_logicaContext ctx) {
        if (ctx.VERDADEIRO() != null) {
            saida.append("true");
        } else if (ctx.FALSO() != null) {
            saida.append("false");
        } else {
            visitExp_relacional(ctx.exp_relacional());
        }
        return null;
    }

    @Override
    public Void visitExp_relacional(JanderParser.Exp_relacionalContext ctx) {
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        if (ctx.exp_aritmetica().size() > 1) {
            String op = ctx.OP_RELAC() != null ? ctx.OP_RELAC().getText() : "==";
            if (op.equals("<>")) op = "!=";
            if (op.equals("=")) op = "==";
            
            saida.append(" ").append(op).append(" ");
            visitExp_aritmetica(ctx.exp_aritmetica(1));
        }
        return null;
    }

    @Override
    public Void visitExp_aritmetica(JanderParser.Exp_aritmeticaContext ctx) {
        for (int i = 0; i < ctx.termo().size(); i++) {
            if (i > 0) {
                saida.append(" ").append(ctx.OP_ARIT_SOMA(i - 1).getText()).append(" ");
            }
            visitTermo(ctx.termo(i));
        }
        return null;
    }

    @Override
    public Void visitTermo(JanderParser.TermoContext ctx) {
        for (int i = 0; i < ctx.fator().size(); i++) {
            if (i > 0) {
                saida.append(" ").append(ctx.OP_ARIT_MULT(i - 1).getText()).append(" ");
            }
            visitFator(ctx.fator(i));
        }
        return null;
    }

    @Override
    public Void visitFator(JanderParser.FatorContext ctx) {
        for (int i = 0; i < ctx.parcela().size(); i++) {
            if (i > 0) {
                saida.append(" % "); 
            }
            visitParcela(ctx.parcela(i));
        }
        return null;
    }

    @Override
    public Void visitParcela(JanderParser.ParcelaContext ctx) {
        if (ctx.OP_ARIT_SOMA() != null) {
            saida.append(ctx.OP_ARIT_SOMA().getText());
        }
        if (ctx.parcela_unario() != null) {
            visitParcela_unario(ctx.parcela_unario());
        } else {
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
        return null;
    }

    @Override
    public Void visitParcela_unario(JanderParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            saida.append(ctx.NUM_INT().getText());
        } else if (ctx.NUM_REAL() != null) {
            saida.append(ctx.NUM_REAL().getText());
        } else if (ctx.identificador() != null) {
            if (ctx.PONTEIRO_OP() != null) saida.append("*");
            visitIdentificador(ctx.identificador());
        } else if (ctx.ABREPAR() != null) {
            saida.append("(");
            visitExpressao(ctx.expressao(0));
            saida.append(")");
        } else if (ctx.IDENT() != null) { 
            saida.append(ctx.IDENT().getText()).append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                visitExpressao(ctx.expressao(i));
                if (i < ctx.expressao().size() - 1) saida.append(", ");
            }
            saida.append(")");
        }
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) {
            saida.append(ctx.CADEIA().getText());
        } else if (ctx.identificador() != null) {
            saida.append("&"); 
            visitIdentificador(ctx.identificador());
        }
        return null;
    }

    @Override
    public Void visitIdentificador(JanderParser.IdentificadorContext ctx) {
        saida.append(ctx.getText()); 
        return null;
    }

    // --- MÉTODOS AUXILIARES ---
    private String tokenTipoParaC(String tipoJander) {
        switch (tipoJander) {
            case "inteiro": return "int";
            case "real": return "float";
            case "literal": return "char";
            case "logico": return "bool";
            default: return tipoJander; 
        }
    }

    private String obterFormatScanfPrintf(TipoLA tipo) {
        switch (tipo) {
            case INTEIRO: return "%d";
            case REAL: return "%f";
            case LITERAL: return "%s";
            case LOGICO: return "%d";
            default: return "%d";
        }
    }
}