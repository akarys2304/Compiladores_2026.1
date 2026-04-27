package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.CategoriaSimbolos;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;

public class AnalisadorSemantico extends JanderBaseVisitor<Void> {

    private Escopos escopos;

    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos();
        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitDeclaracao_local(JanderParser.Declaracao_localContext ctx) {
        if (ctx.DECLARE() != null && ctx.variavel() != null) {
            processarDeclaracaoVariavel(ctx.variavel(), CategoriaSimbolos.VARIAVEL);
        } else if (ctx.CONSTANTE() != null) {
            String nome = ctx.IDENT().getText();
            String strTipo = ctx.tipo_basico().getText();
            TipoLA tipo = AnalisadorSemanticoUtils.stringParaTipo(strTipo, false);
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.CONSTANTE, null);
        } else if (ctx.TIPO() != null) {
            String nome = ctx.IDENT().getText();
            TipoLA tipo = obterTipoDoContexto(ctx.tipo());
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.TIPO, nome);
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            verificarIdentificadorDeclarado(ident);
        }
        return super.visitCmdLeia(ctx);
    }

    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
        }
        return super.visitCmdEscreva(ctx);
    }

    private void processarDeclaracaoVariavel(JanderParser.VariavelContext ctx,
            CategoriaSimbolos categoria) {
        TipoLA tipo;
        String nomeRegistro = null;

        if (ctx.tipo().registro() != null) {
            tipo = TipoLA.REGISTRO;
        } else {
            JanderParser.Tipo_estendidoContext te = ctx.tipo().tipo_estendido();
            boolean ehPonteiro = te.PONTEIRO_OP() != null;
            JanderParser.Tipo_basico_identContext tbi = te.tipo_basico_ident();

            if (tbi.tipo_basico() != null) {
                tipo = AnalisadorSemanticoUtils.stringParaTipo(
                        tbi.tipo_basico().getText(), ehPonteiro);
            } else {
                String nomeTipo = tbi.IDENT().getText();
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTipo =
                        escopos.verificar(nomeTipo);

                if (entradaTipo == null) {
                    AnalisadorSemanticoUtils.adicionarErroSemantico(
                            tbi.IDENT().getSymbol(),
                            "tipo " + nomeTipo + " nao declarado");
                    tipo = TipoLA.TIPO_INDEFINIDO;
                } else {
                    tipo = entradaTipo.tipo;
                    nomeRegistro = nomeTipo;
                }
            }
        }

        for (var ident : ctx.identificador()) {
            String nome = ident.IDENT(0).getText();
            verificarEInserirSimbolo(ident.IDENT(0).getSymbol(), nome, tipo,
                    categoria, nomeRegistro);
        }
    }

    private void verificarIdentificadorDeclarado(JanderParser.IdentificadorContext ctx) {
        String nome = ctx.IDENT(0).getText();
        if (!escopos.existeEmQualquerEscopo(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT(0).getSymbol(),
                    "identificador " + nome + " nao declarado");
        }
    }

    private void verificarEInserirSimbolo(org.antlr.v4.runtime.Token token,
            String nome, TipoLA tipo, CategoriaSimbolos categoria,
            String nomeRegistro) {

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(token,
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionar(nome, tipo, categoria, nomeRegistro);
        }
    }

    private TipoLA obterTipoDoContexto(JanderParser.TipoContext ctx) {
        if (ctx.registro() != null) {
            return TipoLA.REGISTRO;
        }
        var te = ctx.tipo_estendido();
        boolean ehPonteiro = te.PONTEIRO_OP() != null;
        if (te.tipo_basico_ident().tipo_basico() != null) {
            return AnalisadorSemanticoUtils.stringParaTipo(te.tipo_basico_ident().tipo_basico().getText(), ehPonteiro);
        }
        return TipoLA.TIPO_INDEFINIDO;
    }
}