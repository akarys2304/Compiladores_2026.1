package br.ufscar.dc.compiladores.t4;

import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.CategoriaSimbolos;
import br.ufscar.dc.compiladores.t4.TabelaDeSimbolos.TipoLA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AnalisadorSemantico extends JanderBaseVisitor<Void> {

    private Escopos escopos;
    private Stack<Boolean> escopoPermiteRetorno = new Stack<>();

    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos();
        escopoPermiteRetorno.clear();
        escopoPermiteRetorno.push(false);
        return super.visitPrograma(ctx);
    }

    @Override
    public Void visitDeclaracao_local(JanderParser.Declaracao_localContext ctx) {
        // DECLARE
        if (ctx.DECLARE() != null && ctx.variavel() != null) {
            processarDeclaracaoVariavel(ctx.variavel(), CategoriaSimbolos.VARIAVEL);
        }
        // CONSTANTE
        else if (ctx.CONSTANTE() != null) {
            String nome = ctx.IDENT().getText();
            TipoLA tipo = AnalisadorSemanticoUtils.stringParaTipo(ctx.tipo_basico().getText(), false);
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo, CategoriaSimbolos.CONSTANTE, null, null);
        }
        // TIPO
        else if (ctx.TIPO() != null) {
            String nomeTipo = ctx.IDENT().getText();
            Map<String, TipoLA> campos = new HashMap<>();

            if (ctx.tipo().registro() != null) {
                for (var variavel : ctx.tipo().registro().variavel()) {
                    TipoLA tipoCampo = obterTipoVariavel(variavel);
                    for (var ident : variavel.identificador()) {
                        campos.put(ident.IDENT(0).getText(), tipoCampo);
                    }
                }
            }
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nomeTipo, TipoLA.REGISTRO, CategoriaSimbolos.TIPO, nomeTipo, campos);
        }
        return null;
    }

    @Override
    public Void visitDeclaracao_global(JanderParser.Declaracao_globalContext ctx) {
        if (ctx.procedimento() != null) {
            visitarProcedimento(ctx.procedimento());
        } else if (ctx.funcao() != null) {
            visitarFuncao(ctx.funcao());
        }
        return null;
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
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionarFuncaoOuProcedimento(nome, TipoLA.VOID, CategoriaSimbolos.PROCEDIMENTO, tiposParam, nomesRegParam);
        }

        escopos.criarNovoEscopo();
        escopoPermiteRetorno.push(false);

        if (ctx.parametros() != null) {
            inserirParametrosNoEscopo(ctx.parametros());
        }

        for (var decl : ctx.declaracao_local()) visit(decl);
        for (var cmd : ctx.cmd()) visit(cmd);

        escopoPermiteRetorno.pop();
        escopos.abandonarEscopo();
    }

    private void visitarFuncao(JanderParser.FuncaoContext ctx) {
        String nome = ctx.IDENT().getText();
        TipoLA tipoRetorno = obterTipoEstendido(ctx.tipo_estendido());
        List<TipoLA> tiposParam = new ArrayList<>();
        List<String> nomesRegParam = new ArrayList<>();

        if (ctx.parametros() != null) {
            coletarTiposParametros(ctx.parametros(), tiposParam, nomesRegParam);
        }

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(), "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionarFuncaoOuProcedimento(nome, tipoRetorno, CategoriaSimbolos.FUNCAO, tiposParam, nomesRegParam);
        }

        escopos.criarNovoEscopo();
        escopoPermiteRetorno.push(true);

        if (ctx.parametros() != null) {
            inserirParametrosNoEscopo(ctx.parametros());
        }

        for (var decl : ctx.declaracao_local()) visit(decl);
        for (var cmd : ctx.cmd()) visit(cmd);

        escopoPermiteRetorno.pop();
        escopos.abandonarEscopo();
    }

    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            verificarIdentificadorDeclarado(ident);
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
        }
        return null;
    }

    @Override
    public Void visitCmdAtribuicao(JanderParser.CmdAtribuicaoContext ctx) {
        boolean ehDesreferenciacao = ctx.PONTEIRO_OP() != null;
        String prefixo = ehDesreferenciacao ? "^" : "";
        String nome = prefixo + identificadorCompleto(ctx.identificador());

        TipoLA tipoEsq = AnalisadorSemanticoUtils.verificarTipoIdentificador(
                escopos, ctx.identificador(), ehDesreferenciacao
        );
        TipoLA tipoDir = AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());

        boolean destinoEhPonteiroBruto = 
            tipoEsq == TipoLA.PONTEIRO_INTEIRO || 
            tipoEsq == TipoLA.PONTEIRO_REAL || 
            tipoEsq == TipoLA.PONTEIRO_LITERAL || 
            tipoEsq == TipoLA.PONTEIRO_LOGICO || 
            tipoEsq == TipoLA.PONTEIRO_REGISTRO;

        if (!ehDesreferenciacao && destinoEhPonteiroBruto) {
            return null;
        }

        if (!AnalisadorSemanticoUtils.tiposCompativeisAtribuicao(tipoEsq, null, tipoDir, null)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.identificador().IDENT(0).getSymbol(),
                    "atribuicao nao compativel para " + nome
            );
        }
        return null;
    }

    @Override
    public Void visitCmdRetorne(JanderParser.CmdRetorneContext ctx) {
        if (escopoPermiteRetorno.isEmpty() || !escopoPermiteRetorno.peek()) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.RETORNE().getSymbol(),
                    "comando retorne nao permitido nesse escopo"
            );
        }
        return null;
    }

    private void processarDeclaracaoVariavel(JanderParser.VariavelContext ctx, CategoriaSimbolos categoria) {
        TipoLA tipo = obterTipoVariavel(ctx);
        Map<String, TipoLA> campos = null;
        String nomeTipoMestre = null;

        if (ctx.tipo().tipo_estendido() != null 
                && ctx.tipo().tipo_estendido().tipo_basico_ident().IDENT() != null) {
            nomeTipoMestre = ctx.tipo().tipo_estendido().tipo_basico_ident().IDENT().getText();
            var entradaTipo = escopos.verificar(nomeTipoMestre);
            if (entradaTipo != null) {
                campos = entradaTipo.camposRegistro;
            }
        }

        if (ctx.tipo().registro() != null) {
            campos = new HashMap<>();
            for (var varReg : ctx.tipo().registro().variavel()) {
                TipoLA tipoCampo = obterTipoVariavel(varReg);
                for (var ident : varReg.identificador()) {
                    campos.put(ident.IDENT(0).getText(), tipoCampo);
                }
            }
        }

        for (var ident : ctx.identificador()) {
            verificarEInserirSimbolo(
                    ident.IDENT(0).getSymbol(),
                    ident.IDENT(0).getText(),
                    tipo,
                    categoria,
                    nomeTipoMestre, // Salva o tipo mestre para amparar aninhamentos futuros
                    campos
                );
        }
    }

    private TipoLA obterTipoVariavel(JanderParser.VariavelContext ctx) {
        if (ctx.tipo().registro() != null) {
            return TipoLA.REGISTRO;
        }
        return obterTipoEstendido(ctx.tipo().tipo_estendido());
    }

    private TipoLA obterTipoEstendido(JanderParser.Tipo_estendidoContext ctx) {
        boolean ponteiro = ctx.PONTEIRO_OP() != null;
        if (ctx.tipo_basico_ident().tipo_basico() != null) {
            return AnalisadorSemanticoUtils.stringParaTipo(ctx.tipo_basico_ident().tipo_basico().getText(), ponteiro);
        }
        return TipoLA.REGISTRO;
    }

    private void coletarTiposParametros(JanderParser.ParametrosContext ctx, List<TipoLA> tiposParam, List<String> nomesRegParam) {
        for (var param : ctx.parametro()) {
            TipoLA tipo = obterTipoEstendido(param.tipo_estendido());
            for (var ident : param.identificador()) {
                tiposParam.add(tipo);
                nomesRegParam.add(null);
            }
        }
    }

    private void inserirParametrosNoEscopo(JanderParser.ParametrosContext ctx) {
        for (var param : ctx.parametro()) {
            TipoLA tipo = obterTipoEstendido(param.tipo_estendido());
            Map<String, TipoLA> campos = null;
            String nomeTipoMestre = null;

            if (param.tipo_estendido().tipo_basico_ident().IDENT() != null) {
                nomeTipoMestre = param.tipo_estendido().tipo_basico_ident().IDENT().getText();
                var entradaTipo = escopos.verificar(nomeTipoMestre);
                if (entradaTipo != null) {
                    campos = entradaTipo.camposRegistro;
                }
            }

            for (var ident : param.identificador()) {
                escopos.obterEscopoAtual().adicionar(
                        ident.IDENT(0).getText(),
                        tipo,
                        CategoriaSimbolos.PARAMETRO,
                        nomeTipoMestre,
                        campos
                );
            }
        }
    }

    private void verificarIdentificadorDeclarado(JanderParser.IdentificadorContext ctx) {
        String nome = ctx.IDENT(0).getText();
        if (!escopos.existeEmQualquerEscopo(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.IDENT(0).getSymbol(),
                    "identificador " + identificadorCompleto(ctx) + " nao declarado"
            );
        }
    }

    private void verificarEInserirSimbolo(org.antlr.v4.runtime.Token token, String nome, TipoLA tipo, CategoriaSimbolos categoria, String nomeRegistro, Map<String, TipoLA> campos) {
        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(token, "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionar(nome, tipo, categoria, nomeRegistro, campos);
        }
    }

    private String identificadorCompleto(JanderParser.IdentificadorContext ctx) {
        return ctx.getText();
    }
}