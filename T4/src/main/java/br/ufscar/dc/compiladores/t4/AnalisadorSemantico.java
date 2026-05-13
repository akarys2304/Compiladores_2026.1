package br.ufscar.dc.compiladores.t4;

import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.CategoriaSimbolos;
import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.TipoLA;
import java.util.ArrayList;
import java.util.List;


public class AnalisadorSemantico extends JanderBaseVisitor<Void> {

    private Escopos escopos;

    
    private boolean dentroFuncao = false;

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
            // Define um novo tipo customizado (normalmente um registro)
            String nome = ctx.IDENT().getText();
            TipoLA tipo = obterTipoDoContexto(ctx.tipo());
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.TIPO, nome);
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitDeclaracao_global(JanderParser.Declaracao_globalContext ctx) {
        if (ctx.procedimento() != null) {
            visitarProcedimento(ctx.procedimento());
        } else if (ctx.funcao() != null) {
            visitarFuncao(ctx.funcao());
        }
        return null; // não chama super: tratamos manualmente
    }

    private void visitarProcedimento(JanderParser.ProcedimentoContext ctx) {
        String nome = ctx.IDENT().getText();

        List<TipoLA> tiposParam = new ArrayList<>();
        List<String> nomesRegParam = new ArrayList<>();
        if (ctx.parametros() != null) {
            coletarTiposParametros(ctx.parametros(), tiposParam, nomesRegParam);
        }

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionarFuncaoOuProcedimento(nome, TipoLA.VOID,
                    CategoriaSimbolos.PROCEDIMENTO, tiposParam, nomesRegParam);
        }

        escopos.criarNovoEscopo();
        boolean estadoAnterior = dentroFuncao;
        dentroFuncao = false; 

        if (ctx.parametros() != null) {
            inserirParametrosNoEscopo(ctx.parametros());
        }

        for (var decl : ctx.declaracao_local()) {
            visit(decl);
        }
        for (var cmd : ctx.cmd()) {
            visit(cmd);
        }

        dentroFuncao = estadoAnterior;
        escopos.abandonarEscopo();
    }

    private void visitarFuncao(JanderParser.FuncaoContext ctx) {
        String nome = ctx.IDENT().getText();

        JanderParser.Tipo_estendidoContext te = ctx.tipo_estendido();
        boolean ehPonteiro = te.PONTEIRO_OP() != null;
        TipoLA tipoRetorno;
        if (te.tipo_basico_ident().tipo_basico() != null) {
            tipoRetorno = AnalisadorSemanticoUtils.stringParaTipo(
                    te.tipo_basico_ident().tipo_basico().getText(), ehPonteiro);
        } else {
            String nomeTipo = te.tipo_basico_ident().IDENT().getText();
            var entradaTipo = escopos.verificar(nomeTipo);
            tipoRetorno = (entradaTipo != null) ? entradaTipo.tipo : TipoLA.TIPO_INDEFINIDO;
        }

        List<TipoLA> tiposParam = new ArrayList<>();
        List<String> nomesRegParam = new ArrayList<>();
        if (ctx.parametros() != null) {
            coletarTiposParametros(ctx.parametros(), tiposParam, nomesRegParam);
        }

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionarFuncaoOuProcedimento(nome, tipoRetorno,
                    CategoriaSimbolos.FUNCAO, tiposParam, nomesRegParam);
        }

        escopos.criarNovoEscopo();
        boolean estadoAnterior = dentroFuncao;
        dentroFuncao = true; 

        if (ctx.parametros() != null) {
            inserirParametrosNoEscopo(ctx.parametros());
        }

        for (var decl : ctx.declaracao_local()) {
            visit(decl);
        }
        for (var cmd : ctx.cmd()) {
            visit(cmd);
        }

        dentroFuncao = estadoAnterior;
        escopos.abandonarEscopo();
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

    @Override
    public Void visitCmdAtribuicao(JanderParser.CmdAtribuicaoContext ctx) {
        String nome = ctx.identificador().IDENT(0).getText();
        var entrada = escopos.verificar(nome);

        if (entrada == null) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.identificador().IDENT(0).getSymbol(),
                    "identificador " + nome + " nao declarado");
        } else {
            TipoLA tipoVar = entrada.tipo;
            String nomeRegVar = entrada.nomeRegistro;

            if (ctx.PONTEIRO_OP() != null) {
                tipoVar = AnalisadorSemanticoUtils.desreferenciarPonteiro(tipoVar);
                nomeRegVar = null;
            }

            TipoLA tipoExpr = AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());
            String nomeRegExpr = obterNomeRegistroExpressao(ctx.expressao());

            if (!AnalisadorSemanticoUtils.tiposCompativeisAtribuicao(
                    tipoVar, nomeRegVar, tipoExpr, nomeRegExpr)) {
                AnalisadorSemanticoUtils.adicionarErroSemantico(
                        ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + nome);
            }
        }
        return super.visitCmdAtribuicao(ctx);
    }

    
    @Override
    public Void visitCmdChamada(JanderParser.CmdChamadaContext ctx) {
        String nome = ctx.IDENT().getText();
        var entrada = escopos.verificar(nome);

        if (entrada == null) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "identificador " + nome + " nao declarado");
            return super.visitCmdChamada(ctx);
        }

        int numArgs = ctx.expressao().size();
        int numParams = entrada.tiposParametros.size();

        if (numArgs != numParams) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "incompatibilidade de parametros na chamada de " + nome);
            return super.visitCmdChamada(ctx);
        }

        boolean incompativel = false;
        for (int i = 0; i < numArgs; i++) {
            TipoLA tipoArg = AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao(i));
            String nomeRegArg = obterNomeRegistroExpressao(ctx.expressao(i));
            TipoLA tipoParam = entrada.tiposParametros.get(i);
            String nomeRegParam = entrada.nomesRegistroParametros.get(i);

            if (!AnalisadorSemanticoUtils.tiposCompativeisParametro(
                    tipoParam, nomeRegParam, tipoArg, nomeRegArg)) {
                incompativel = true;
                break;
            }
        }

        if (incompativel) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "incompatibilidade de parametros na chamada de " + nome);
        }

        return super.visitCmdChamada(ctx);
    }

    
    @Override
    public Void visitCmdRetorne(JanderParser.CmdRetorneContext ctx) {
        if (!dentroFuncao) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.RETORNE().getSymbol(),
                    "comando retorne nao permitido nesse escopo");
        }
        return super.visitCmdRetorne(ctx);
    }

    @Override
    public Void visitCmdEnquanto(JanderParser.CmdEnquantoContext ctx) {
        AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());
        return super.visitCmdEnquanto(ctx);
    }

    
    @Override
    public Void visitCmdSe(JanderParser.CmdSeContext ctx) {
        AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());
        return super.visitCmdSe(ctx);
    }

    @Override
    public Void visitCmdFaca(JanderParser.CmdFacaContext ctx) {
        AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());
        return super.visitCmdFaca(ctx);
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
                var entradaTipo = escopos.verificar(nomeTipo);

                if (entradaTipo == null) {
                    AnalisadorSemanticoUtils.adicionarErroSemantico(
                            tbi.IDENT().getSymbol(),
                            "tipo " + nomeTipo + " nao declarado");
                    tipo = TipoLA.TIPO_INDEFINIDO;
                } else {
                    tipo = ehPonteiro ? TipoLA.PONTEIRO_REGISTRO : entradaTipo.tipo;
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

   
    private void coletarTiposParametros(JanderParser.ParametrosContext ctx,
            List<TipoLA> tiposParam, List<String> nomesRegParam) {

        for (var param : ctx.parametro()) {
            JanderParser.Tipo_estendidoContext te = param.tipo_estendido();
            boolean ehPonteiro = te.PONTEIRO_OP() != null;
            JanderParser.Tipo_basico_identContext tbi = te.tipo_basico_ident();

            TipoLA tipo;
            String nomeReg = null;

            if (tbi.tipo_basico() != null) {
                tipo = AnalisadorSemanticoUtils.stringParaTipo(
                        tbi.tipo_basico().getText(), ehPonteiro);
            } else {
                String nomeTipo = tbi.IDENT().getText();
                var entradaTipo = escopos.verificar(nomeTipo);
                if (entradaTipo != null) {
                    tipo = ehPonteiro ? TipoLA.PONTEIRO_REGISTRO : entradaTipo.tipo;
                    nomeReg = nomeTipo;
                } else {
                    tipo = TipoLA.TIPO_INDEFINIDO;
                }
            }

            for (var ident : param.identificador()) {
                tiposParam.add(tipo);
                nomesRegParam.add(nomeReg);
            }
        }
    }

    
    private void inserirParametrosNoEscopo(JanderParser.ParametrosContext ctx) {
        for (var param : ctx.parametro()) {
            JanderParser.Tipo_estendidoContext te = param.tipo_estendido();
            boolean ehPonteiro = te.PONTEIRO_OP() != null;
            JanderParser.Tipo_basico_identContext tbi = te.tipo_basico_ident();

            TipoLA tipo;
            String nomeReg = null;

            if (tbi.tipo_basico() != null) {
                tipo = AnalisadorSemanticoUtils.stringParaTipo(
                        tbi.tipo_basico().getText(), ehPonteiro);
            } else {
                String nomeTipo = tbi.IDENT().getText();
                var entradaTipo = escopos.verificar(nomeTipo);
                if (entradaTipo != null) {
                    tipo = ehPonteiro ? TipoLA.PONTEIRO_REGISTRO : entradaTipo.tipo;
                    nomeReg = nomeTipo;
                } else {
                    tipo = TipoLA.TIPO_INDEFINIDO;
                }
            }

            for (var ident : param.identificador()) {
                String nomeParam = ident.IDENT(0).getText();
                TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
                if (!escopoAtual.existe(nomeParam)) {
                    escopoAtual.adicionar(nomeParam, tipo,
                            CategoriaSimbolos.PARAMETRO, nomeReg);
                }
            }
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
        if (ctx.registro() != null) return TipoLA.REGISTRO;
        var te = ctx.tipo_estendido();
        boolean ehPonteiro = te.PONTEIRO_OP() != null;
        if (te.tipo_basico_ident().tipo_basico() != null) {
            return AnalisadorSemanticoUtils.stringParaTipo(
                    te.tipo_basico_ident().tipo_basico().getText(), ehPonteiro);
        }
        return TipoLA.TIPO_INDEFINIDO;
    }

    
    private String obterNomeRegistroExpressao(JanderParser.ExpressaoContext ctx) {
        try {
            var tl = ctx.termo_logico(0);
            if (tl == null) return null;
            var fl = tl.fator_logico(0);
            if (fl == null) return null;
            var pl = fl.parcela_logica();
            if (pl.exp_relacional() == null) return null;
            var er = pl.exp_relacional();
            if (er.exp_aritmetica().size() != 1) return null;
            var ea = er.exp_aritmetica(0);
            if (ea.termo().size() != 1) return null;
            var t = ea.termo(0);
            if (t.fator().size() != 1) return null;
            var f = t.fator(0);
            if (f.parcela().size() != 1) return null;
            var p = f.parcela(0);
            if (p.parcela_unario() == null) return null;
            var pu = p.parcela_unario();
            if (pu.identificador() == null) return null;

            String nome = pu.identificador().IDENT(0).getText();
            var entrada = escopos.verificar(nome);
            return (entrada != null) ? entrada.nomeRegistro : null;
        } catch (Exception e) {
            return null;
        }
    }
}