package br.ufscar.dc.compiladores.t5;

import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.TipoLA;
import br.ufscar.dc.compiladores.t5.TabelaDeSimbolos.CategoriaSimbolos;

public class JanderGeradorC extends JanderBaseVisitor<Void> {

    private final StringBuilder saida;
    private final StringBuilder defines;
    private Escopos escopos;

    public JanderGeradorC() {
        this.saida = new StringBuilder();
        this.defines = new StringBuilder();
    }

    public String getCodigoGerado() {
        return saida.toString();
    }

    // --- PROGRAMA PRINCIPAL ---
    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos();

        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n");
        saida.append("#include <stdbool.h>\n");

        if (ctx.declaracoes() != null) {
            visitDeclaracoes(ctx.declaracoes());
        }

        if (defines.length() > 0) {
            saida.append(defines);
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

        // CONSTANTE → #define
        if (ctx.CONSTANTE() != null) {
            String nome = ctx.IDENT().getText();
            String valor = ctx.valor_constante().getText();
            defines.append("#define ").append(nome).append(" ").append(valor).append("\n");
            return null;
        }

        // TIPO nomeTipo : registro ... fim_registro → typedef struct
        if (ctx.TIPO() != null && ctx.IDENT() != null
                && ctx.tipo() != null && ctx.tipo().registro() != null) {
            String nomeTipo = ctx.IDENT().getText();
            java.util.Map<String, TipoLA> campos = new java.util.LinkedHashMap<>();

            saida.append("\ttypedef struct {\n");
            for (var varReg : ctx.tipo().registro().variavel()) {
                TipoLA tipoCampo = AnalisadorSemanticoUtils.stringParaTipo(varReg.tipo().getText(), false);
                String tipoCampoC = tokenTipoParaC(varReg.tipo().getText());
                for (var identCampo : varReg.identificador()) {
                    String nomeCampo = identCampo.IDENT(0).getText();
                    campos.put(nomeCampo, tipoCampo);
                    saida.append("\t\t").append(tipoCampoC).append(" ").append(nomeCampo);
                    if (tipoCampo == TipoLA.LITERAL) saida.append("[80]");
                    saida.append(";\n");
                }
            }
            saida.append("\t} ").append(nomeTipo).append(";\n");
            escopos.obterEscopoAtual().adicionar(
                    nomeTipo, TipoLA.REGISTRO, CategoriaSimbolos.TIPO, nomeTipo, campos);
            return null;
        }

        // DECLARE variavel : registro anônimo inline → struct { } reg;
        if (ctx.DECLARE() != null && ctx.variavel() != null
                && ctx.variavel().tipo().registro() != null) {
            java.util.Map<String, TipoLA> campos = new java.util.LinkedHashMap<>();

            for (var ident : ctx.variavel().identificador()) {
                String nomeVar = ident.IDENT(0).getText();
                campos.clear();
                saida.append("\tstruct {\n");
                for (var varReg : ctx.variavel().tipo().registro().variavel()) {
                    TipoLA tipoCampo = AnalisadorSemanticoUtils.stringParaTipo(varReg.tipo().getText(), false);
                    String tipoCampoC = tokenTipoParaC(varReg.tipo().getText());
                    for (var identCampo : varReg.identificador()) {
                        String nomeCampo = identCampo.IDENT(0).getText();
                        campos.put(nomeCampo, tipoCampo);
                        saida.append("\t\t").append(tipoCampoC).append(" ").append(nomeCampo);
                        if (tipoCampo == TipoLA.LITERAL) saida.append("[80]");
                        saida.append(";\n");
                    }
                }
                saida.append("\t} ").append(nomeVar).append(";\n");
                escopos.obterEscopoAtual().adicionar(
                        nomeVar, TipoLA.REGISTRO, CategoriaSimbolos.VARIAVEL,
                        null, new java.util.LinkedHashMap<>(campos));
            }
            return null;
        }

        // DECLARE variavel : tipo simples ou tipo nomeado
        if (ctx.DECLARE() != null && ctx.variavel() != null) {
            String strTipoJander = ctx.variavel().tipo().getText();
            boolean ehPonteiro = strTipoJander.startsWith("^");
            String strTipoBase = ehPonteiro ? strTipoJander.substring(1) : strTipoJander;
            TipoLA tipoEnum = AnalisadorSemanticoUtils.stringParaTipo(strTipoBase, ehPonteiro);
            String strTipoC = tokenTipoParaC(strTipoJander);

            java.util.Map<String, TipoLA> camposHerdados = null;
            String nomeTipoMestre = null;
            if (tipoEnum == TipoLA.REGISTRO) {
                nomeTipoMestre = strTipoBase;
                var entradaTipo = escopos.verificar(nomeTipoMestre);
                if (entradaTipo != null) camposHerdados = entradaTipo.camposRegistro;
            }

            for (int i = 0; i < ctx.variavel().identificador().size(); i++) {
                var ident = ctx.variavel().identificador(i);
                String nomeVar = ident.IDENT(0).getText();

                escopos.obterEscopoAtual().adicionar(
                        nomeVar, tipoEnum, CategoriaSimbolos.VARIAVEL,
                        nomeTipoMestre, camposHerdados);

                saida.append("\t").append(strTipoC).append(" ");
                visitIdentificador(ident);
                if (tipoEnum == TipoLA.LITERAL) saida.append("[80]");
                saida.append(";\n");
            }
        }

        return null;
    }

    // --- COMANDOS DE CONTROLE ---
    @Override
    public Void visitCmdSe(JanderParser.CmdSeContext ctx) {
        saida.append("\tif (");
        visitExpressao(ctx.expressao());
        saida.append(") {\n");

        java.util.List<JanderParser.CmdContext> cmdEntao = new java.util.ArrayList<>();
        java.util.List<JanderParser.CmdContext> cmdSenao = new java.util.ArrayList<>();
        boolean aposSenao = false;
        for (var child : ctx.children) {
            if (child instanceof org.antlr.v4.runtime.tree.TerminalNode) {
                org.antlr.v4.runtime.tree.TerminalNode tn = (org.antlr.v4.runtime.tree.TerminalNode) child;
                if (tn.getSymbol().getType() == JanderLexer.SENAO) aposSenao = true;
            } else if (child instanceof JanderParser.CmdContext) {
                if (aposSenao) cmdSenao.add((JanderParser.CmdContext) child);
                else cmdEntao.add((JanderParser.CmdContext) child);
            }
        }

        for (var cmd : cmdEntao) { saida.append("\t"); visit(cmd); }
        saida.append("\t}");

        if (!cmdSenao.isEmpty()) {
            saida.append(" else {\n");
            for (var cmd : cmdSenao) { saida.append("\t"); visit(cmd); }
            saida.append("\t}");
        }
        saida.append("\n");
        return null;
    }

    @Override
    public Void visitCmdEnquanto(JanderParser.CmdEnquantoContext ctx) {
        saida.append("\twhile (");
        visitExpressao(ctx.expressao());
        saida.append(") {\n");
        for (var cmd : ctx.cmd()) { saida.append("\t"); visit(cmd); }
        saida.append("\t}\n");
        return null;
    }

    @Override
    public Void visitCmdPara(JanderParser.CmdParaContext ctx) {
        String ident = ctx.IDENT().getText();
        saida.append("\tfor (").append(ident).append(" = ");
        visitExp_aritmetica(ctx.exp_aritmetica(0));
        saida.append("; ").append(ident).append(" <= ");
        visitExp_aritmetica(ctx.exp_aritmetica(1));
        saida.append("; ").append(ident).append("++) {\n");
        for (var cmd : ctx.cmd()) { saida.append("\t"); visit(cmd); }
        saida.append("\t}\n");
        return null;
    }

    @Override
    public Void visitCmdFaca(JanderParser.CmdFacaContext ctx) {
        saida.append("\tdo {\n");
        for (var cmd : ctx.cmd()) { saida.append("\t"); visit(cmd); }
        saida.append("\t} while (");
        visitExpressao(ctx.expressao());
        saida.append(");\n");
        return null;
    }

    @Override
    public Void visitCmdAtribuicao(JanderParser.CmdAtribuicaoContext ctx) {
        boolean ehDesref = ctx.PONTEIRO_OP() != null;
        TipoLA tipoDestino = AnalisadorSemanticoUtils.verificarTipoIdentificador(
                escopos, ctx.identificador(), ehDesref);

        if (tipoDestino == TipoLA.LITERAL) {
            saida.append("\tstrcpy(");
            if (ehDesref) saida.append("*");
            visitIdentificador(ctx.identificador());
            saida.append(", ");
            visitExpressao(ctx.expressao());
            saida.append(");\n");
        } else {
            saida.append("\t");
            if (ehDesref) saida.append("*");
            visitIdentificador(ctx.identificador());
            saida.append(" = ");
            visitExpressao(ctx.expressao());
            saida.append(";\n");
        }
        return null;
    }

    @Override
    public Void visitCmdChamada(JanderParser.CmdChamadaContext ctx) {
        saida.append("\t").append(ctx.IDENT().getText()).append("(");
        for (int i = 0; i < ctx.expressao().size(); i++) {
            if (i > 0) saida.append(", ");
            visitExpressao(ctx.expressao(i));
        }
        saida.append(");\n");
        return null;
    }

    @Override
    public Void visitCmdRetorne(JanderParser.CmdRetorneContext ctx) {
        saida.append("\treturn ");
        visitExpressao(ctx.expressao());
        saida.append(";\n");
        return null;
    }

    @Override
    public Void visitCmdCaso(JanderParser.CmdCasoContext ctx) {
        saida.append("\tswitch (");
        visitExp_aritmetica(ctx.exp_aritmetica());
        saida.append(") {\n");
        visitSelecao(ctx.selecao());

        boolean temSenao = false;
        java.util.List<JanderParser.CmdContext> cmdsSenao = new java.util.ArrayList<>();
        boolean apos = false;
        for (var child : ctx.children) {
            if (child instanceof org.antlr.v4.runtime.tree.TerminalNode) {
                org.antlr.v4.runtime.tree.TerminalNode tn = (org.antlr.v4.runtime.tree.TerminalNode) child;
                if (tn.getSymbol().getType() == JanderLexer.SENAO) { temSenao = true; apos = true; }
            } else if (apos && child instanceof JanderParser.CmdContext) {
                cmdsSenao.add((JanderParser.CmdContext) child);
            }
        }
        if (temSenao) {
            saida.append("\t\tdefault:\n");
            for (var cmd : cmdsSenao) { saida.append("\t\t"); visit(cmd); }
        }
        saida.append("\t}\n");
        return null;
    }

    @Override
    public Void visitSelecao(JanderParser.SelecaoContext ctx) {
        for (var item : ctx.item_selecao()) visitItem_selecao(item);
        return null;
    }

    @Override
    public Void visitItem_selecao(JanderParser.Item_selecaoContext ctx) {
        for (var intervalo : ctx.constantes().numero_intervalo()) {
            if (intervalo.PONTOS_INTERVALO() != null) {
                int ini = Integer.parseInt(intervalo.NUM_INT(0).getText());
                int fim = Integer.parseInt(intervalo.NUM_INT(1).getText());
                for (int v = ini; v <= fim; v++) saida.append("\t\tcase ").append(v).append(":\n");
            } else {
                saida.append("\t\tcase ").append(intervalo.getText()).append(":\n");
            }
        }
        for (var cmd : ctx.cmd()) { saida.append("\t\t\t"); visit(cmd); }
        saida.append("\t\t\tbreak;\n");
        return null;
    }

    // --- DECLARAÇÕES GLOBAIS ---
    @Override
    public Void visitDeclaracoes(JanderParser.DeclaracoesContext ctx) {
        for (var declGlobal : ctx.decl_local_global()) visit(declGlobal);
        return null;
    }

    @Override
    public Void visitDeclaracao_global(JanderParser.Declaracao_globalContext ctx) {
        if (ctx.procedimento() != null) visitProcedimento(ctx.procedimento());
        else if (ctx.funcao() != null) visitFuncao(ctx.funcao());
        return null;
    }

    @Override
    public Void visitProcedimento(JanderParser.ProcedimentoContext ctx) {
        String nome = ctx.IDENT().getText();
        saida.append("void ").append(nome).append("(");
        if (ctx.parametros() != null) gerarParametros(ctx.parametros());
        saida.append(") {\n");
        escopos.criarNovoEscopo();
        if (ctx.parametros() != null) inserirParametrosGerador(ctx.parametros());
        for (var decl : ctx.declaracao_local()) visitDeclaracao_local(decl);
        for (var cmd : ctx.cmd()) visit(cmd);
        escopos.abandonarEscopo();
        saida.append("}\n\n");
        return null;
    }

    @Override
    public Void visitFuncao(JanderParser.FuncaoContext ctx) {
        String nome = ctx.IDENT().getText();
        String tipoC = tokenTipoParaC(ctx.tipo_estendido().tipo_basico_ident().getText());
        saida.append(tipoC).append(" ").append(nome).append("(");
        if (ctx.parametros() != null) gerarParametros(ctx.parametros());
        saida.append(") {\n");
        escopos.criarNovoEscopo();
        if (ctx.parametros() != null) inserirParametrosGerador(ctx.parametros());
        for (var decl : ctx.declaracao_local()) visitDeclaracao_local(decl);
        for (var cmd : ctx.cmd()) visit(cmd);
        escopos.abandonarEscopo();
        saida.append("}\n\n");
        return null;
    }

    private void gerarParametros(JanderParser.ParametrosContext ctx) {
        boolean primeiro = true;
        for (var param : ctx.parametro()) {
            TipoLA tipoEnum = AnalisadorSemanticoUtils.stringParaTipo(
                    param.tipo_estendido().tipo_basico_ident().getText(), false);
            String tipoC = tokenTipoParaC(param.tipo_estendido().tipo_basico_ident().getText());
            for (var ident : param.identificador()) {
                if (!primeiro) saida.append(", ");
                if (tipoEnum == TipoLA.LITERAL) saida.append("char* ").append(ident.getText());
                else saida.append(tipoC).append(" ").append(ident.getText());
                primeiro = false;
            }
        }
    }

    private void inserirParametrosGerador(JanderParser.ParametrosContext ctx) {
        for (var param : ctx.parametro()) {
            TipoLA tipoEnum = AnalisadorSemanticoUtils.stringParaTipo(
                    param.tipo_estendido().tipo_basico_ident().getText(), false);
            for (var ident : param.identificador()) {
                escopos.obterEscopoAtual().adicionar(
                        ident.IDENT(0).getText(), tipoEnum, CategoriaSimbolos.PARAMETRO);
            }
        }
    }

    // --- COMANDOS LEIA / ESCREVA ---
    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            TipoLA tipo = AnalisadorSemanticoUtils.verificarTipoIdentificador(escopos, ident, false);
            String fmt = obterFormatScanfPrintf(tipo);
            saida.append("\tscanf(\"").append(fmt).append("\", ");
            if (tipo == TipoLA.LITERAL) saida.append(ident.getText());
            else saida.append("&").append(ident.getText());
            saida.append(");\n");
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            boolean eCadeiaLiteral = isCadeiaLiteralDireta(expr);
            if (eCadeiaLiteral) {
                saida.append("\tprintf(");
                visitExpressao(expr);
                saida.append(");\n");
            } else {
                TipoLA tipo = AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
                String fmt = obterFormatScanfPrintf(tipo);
                saida.append("\tprintf(\"").append(fmt).append("\", ");
                visitExpressao(expr);
                saida.append(");\n");
            }
        }
        return null;
    }

    private boolean isCadeiaLiteralDireta(JanderParser.ExpressaoContext ctx) {
        try {
            var parcela = ctx.termo_logico(0).fator_logico(0).parcela_logica()
                    .exp_relacional().exp_aritmetica(0).termo(0).fator(0).parcela(0);
            return parcela.parcela_nao_unario() != null
                    && parcela.parcela_nao_unario().CADEIA() != null;
        } catch (Exception e) {
            return false;
        }
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
        if (ctx.OP_BOOL_NAO() != null) saida.append("!");
        visitParcela_logica(ctx.parcela_logica());
        return null;
    }

    @Override
    public Void visitParcela_logica(JanderParser.Parcela_logicaContext ctx) {
        if (ctx.VERDADEIRO() != null) saida.append("true");
        else if (ctx.FALSO() != null) saida.append("false");
        else visitExp_relacional(ctx.exp_relacional());
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
            if (i > 0) saida.append(" ").append(ctx.OP_ARIT_SOMA(i - 1).getText()).append(" ");
            visitTermo(ctx.termo(i));
        }
        return null;
    }

    @Override
    public Void visitTermo(JanderParser.TermoContext ctx) {
        for (int i = 0; i < ctx.fator().size(); i++) {
            if (i > 0) saida.append(" ").append(ctx.OP_ARIT_MULT(i - 1).getText()).append(" ");
            visitFator(ctx.fator(i));
        }
        return null;
    }

    @Override
    public Void visitFator(JanderParser.FatorContext ctx) {
        for (int i = 0; i < ctx.parcela().size(); i++) {
            if (i > 0) saida.append(" % ");
            visitParcela(ctx.parcela(i));
        }
        return null;
    }

    @Override
    public Void visitParcela(JanderParser.ParcelaContext ctx) {
        if (ctx.OP_ARIT_SOMA() != null) saida.append(ctx.OP_ARIT_SOMA().getText());
        if (ctx.parcela_unario() != null) visitParcela_unario(ctx.parcela_unario());
        else visitParcela_nao_unario(ctx.parcela_nao_unario());
        return null;
    }

    @Override
    public Void visitParcela_unario(JanderParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            saida.append(ctx.NUM_INT().getText());
        } else if (ctx.NUM_REAL() != null) {
            saida.append(ctx.NUM_REAL().getText());
        } else if (ctx.IDENT() != null && ctx.ABREPAR() != null) {
            // Chamada de função: deve vir ANTES de identificador
            saida.append(ctx.IDENT().getText()).append("(");
            for (int i = 0; i < ctx.expressao().size(); i++) {
                if (i > 0) saida.append(", ");
                visitExpressao(ctx.expressao(i));
            }
            saida.append(")");
        } else if (ctx.ABREPAR() != null) {
            saida.append("(");
            visitExpressao(ctx.expressao(0));
            saida.append(")");
        } else if (ctx.identificador() != null) {
            if (ctx.PONTEIRO_OP() != null) saida.append("*");
            visitIdentificador(ctx.identificador());
        }
        return null;
    }

    @Override
    public Void visitParcela_nao_unario(JanderParser.Parcela_nao_unarioContext ctx) {
        if (ctx.CADEIA() != null) saida.append(ctx.CADEIA().getText());
        else if (ctx.identificador() != null) {
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
        boolean ponteiro = tipoJander.startsWith("^");
        String base = ponteiro ? tipoJander.substring(1) : tipoJander;
        String tipoC;
        switch (base) {
            case "inteiro": tipoC = "int";   break;
            case "real":    tipoC = "float"; break;
            case "literal": tipoC = "char";  break;
            case "logico":  tipoC = "bool";  break;
            default:        tipoC = base;    break;
        }
        return ponteiro ? tipoC + "*" : tipoC;
    }

    private String obterFormatScanfPrintf(TipoLA tipo) {
        switch (tipo) {
            case INTEIRO: return "%d";
            case REAL:    return "%f";
            case LITERAL: return "%s";
            case LOGICO:  return "%d";
            default:      return "%d";
        }
    }
}