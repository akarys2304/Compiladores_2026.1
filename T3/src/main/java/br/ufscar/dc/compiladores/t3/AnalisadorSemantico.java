package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.CategoriaSimbolos;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;

/**
 * Analisador semântico da Linguagem Algorítmica (LA).
 *
 * Visita a árvore sintática gerada pelo ANTLR e detecta os seguintes erros:
 *   1. Identificador já declarado no mesmo escopo
 *   2. Tipo não declarado
 *   3. Identificador não declarado
 *   4. Atribuição incompatível com o tipo declarado
 *
 * Não interrompe a análise ao encontrar erros — continua até EOF.
 */
public class AnalisadorSemantico extends JanderBaseVisitor<Void> {

    // Pilha de escopos: global + escopos de procedimentos/funções
    private Escopos escopos;

    // ================================================================
    // Programa principal
    // ================================================================

    /**
     * Ponto de entrada: inicializa o escopo global e visita o programa.
     */
    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos();
        return super.visitPrograma(ctx);
    }

    // ================================================================
    // Declarações
    // ================================================================

    /**
     * Declaração de variável: DECLARE identificador(es) : tipo
     * Verifica:
     *   - tipo não declarado (para tipos definidos pelo usuário)
     *   - identificador já declarado no escopo atual
     */
    @Override
    public Void visitDeclaracao_local(JanderParser.Declaracao_localContext ctx) {

        if (ctx.DECLARE() != null && ctx.variavel() != null) {
            // Declaração de variável comum
            processarDeclaracaoVariavel(ctx.variavel(), CategoriaSimbolos.VARIAVEL);

        } else if (ctx.CONSTANTE() != null) {
            // Declaração de constante: CONSTANTE nome : tipo = valor
            String nome = ctx.IDENT().getText();
            String strTipo = ctx.tipo_basico().getText();
            TipoLA tipo = AnalisadorSemanticoUtils.stringParaTipo(strTipo, false);
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.CONSTANTE, null);

        } else if (ctx.TIPO() != null) {
            // Declaração de tipo: TIPO nome : tipo
            String nome = ctx.IDENT().getText();
            // O tipo definido entra como REGISTRO ou o tipo base
            TipoLA tipo = obterTipoDoContexto(ctx.tipo());
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.TIPO, nome);
        }

        return super.visitDeclaracao_local(ctx);
    }

    /**
     * Declaração de procedimento.
     * Insere no escopo externo e cria novo escopo para o corpo.
     */
    @Override
    public Void visitProcedimento(JanderParser.ProcedimentoContext ctx) {
        String nome = ctx.IDENT().getText();

        // Insere o procedimento no escopo atual (antes de criar o escopo interno)
        verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, TipoLA.VOID,
                CategoriaSimbolos.PROCEDIMENTO, null);

        // Novo escopo para parâmetros e corpo do procedimento
        escopos.criarNovoEscopo();

        // Registra os parâmetros no novo escopo
        if (ctx.parametros() != null) {
            for (var param : ctx.parametros().parametro()) {
                processarParametro(param);
            }
        }

        // Visita declarações locais e comandos dentro do procedimento
        for (var decl : ctx.declaracao_local()) {
            visitDeclaracao_local(decl);
        }
        for (var cmd : ctx.cmd()) {
            visitCmd(cmd);
        }

        escopos.abandonarEscopo();
        // Não chama super para evitar dupla visita
        return null;
    }

    /**
     * Declaração de função.
     * Semelhante ao procedimento, mas possui tipo de retorno.
     */
    @Override
    public Void visitFuncao(JanderParser.FuncaoContext ctx) {
        String nome = ctx.IDENT().getText();

        // Determina o tipo de retorno da função
        TipoLA tipoRetorno = obterTipoExtendido(ctx.tipo_estendido());

        // Insere a função no escopo externo
        verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipoRetorno,
                CategoriaSimbolos.FUNCAO, null);

        // Novo escopo para parâmetros e corpo
        escopos.criarNovoEscopo();

        if (ctx.parametros() != null) {
            for (var param : ctx.parametros().parametro()) {
                processarParametro(param);
            }
        }

        for (var decl : ctx.declaracao_local()) {
            visitDeclaracao_local(decl);
        }
        for (var cmd : ctx.cmd()) {
            visitCmd(cmd);
        }

        escopos.abandonarEscopo();
        return null;
    }

    // ================================================================
    // Comandos
    // ================================================================

    /**
     * Comando leia: verifica se cada variável foi declarada.
     */
    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            verificarIdentificadorDeclarado(ident);
        }
        return super.visitCmdLeia(ctx);
    }

    /**
     * Comando escreva: verifica os tipos das expressões.
     */
    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
        }
        return super.visitCmdEscreva(ctx);
    }

    /**
     * Comando de atribuição.
     * Verifica:
     *   - identificador de destino declarado
     *   - compatibilidade entre tipo do destino e tipo da expressão
     */
    @Override
    public Void visitCmdAtribuicao(JanderParser.CmdAtribuicaoContext ctx) {
        // Obtém o nome base do identificador de destino
        String nomeBase = ctx.identificador().IDENT(0).getText();
        boolean ehPonteiro = ctx.PONTEIRO_OP() != null;

        TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaDest = escopos.verificar(nomeBase);

        if (entradaDest == null) {
            // Erro 3: identificador não declarado
            AnalisadorSemanticoUtils.adicionarErroSemantico(
                    ctx.identificador().IDENT(0).getSymbol(),
                    "identificador " + nomeBase + " nao declarado");
        } else {
            // Calcula o tipo da expressão à direita
            TipoLA tipoExpr = AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());

            // Verifica compatibilidade de tipos
            if (tipoExpr != TipoLA.TIPO_INDEFINIDO) {
                boolean compativel = false;

                if (ehPonteiro) {
                    // ^var <- expr: destino deve ser ponteiro
                    compativel = AnalisadorSemanticoUtils.ehPonteiro(entradaDest.tipo)
                            && tipoExpr == AnalisadorSemanticoUtils
                                    .tipoBaseDosPonteiros(entradaDest.tipo);
                } else {
                    compativel = AnalisadorSemanticoUtils
                            .tiposCompativeisAtribuicao(entradaDest, tipoExpr);

                    // Caso especial: registro deve ter o mesmo nome de tipo
                    if (compativel && entradaDest.tipo == TipoLA.REGISTRO
                            && tipoExpr == TipoLA.REGISTRO) {
                        // Verificação simplificada — aceita qualquer registro
                        compativel = true;
                    }
                }

                if (!compativel) {
                    AnalisadorSemanticoUtils.adicionarErroSemantico(
                            ctx.identificador().IDENT(0).getSymbol(),
                            "atribuicao nao compativel para " + nomeBase);
                }
            }
        }

        return null; // não chama super para evitar re-verificação
    }

    /**
     * Chamada de procedimento/função como comando.
     * Verifica se o identificador foi declarado.
     */
    @Override
    public Void visitCmdChamada(JanderParser.CmdChamadaContext ctx) {
        String nome = ctx.IDENT().getText();
        if (!escopos.existeEmQualquerEscopo(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                    "identificador " + nome + " nao declarado");
        }
        return super.visitCmdChamada(ctx);
    }

    // ================================================================
    // Expressões — verificação de identificadores não declarados
    // ================================================================

    /**
     * Parcela unária: verifica identificadores e chamadas de função usados
     * em expressões.
     */
    @Override
    public Void visitParcela_unario(JanderParser.Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            verificarIdentificadorDeclarado(ctx.identificador());
        } else if (ctx.IDENT() != null && ctx.ABREPAR() != null) {
            // Chamada de função dentro de expressão
            String nome = ctx.IDENT().getText();
            if (!escopos.existeEmQualquerEscopo(nome)) {
                AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT().getSymbol(),
                        "identificador " + nome + " nao declarado");
            }
        }
        return super.visitParcela_unario(ctx);
    }

    // ================================================================
    // Métodos auxiliares privados
    // ================================================================

    /**
     * Processa a declaração de variável, inserindo cada identificador
     * da lista no escopo atual com o tipo informado.
     */
    private void processarDeclaracaoVariavel(JanderParser.VariavelContext ctx,
            CategoriaSimbolos categoria) {

        // Resolve o tipo declarado
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
                // Tipo definido pelo usuário (IDENT)
                String nomeTipo = tbi.IDENT().getText();
                TabelaDeSimbolos.EntradaTabelaDeSimbolos entradaTipo =
                        escopos.verificar(nomeTipo);

                if (entradaTipo == null) {
                    // Erro 2: tipo não declarado
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

        // Insere cada identificador no escopo atual
        for (var ident : ctx.identificador()) {
            String nome = ident.IDENT(0).getText();
            verificarEInserirSimbolo(ident.IDENT(0).getSymbol(), nome, tipo,
                    categoria, nomeRegistro);
        }

        // Se for registro, processa seus campos em sub-escopo (simplificado)
        if (ctx.tipo().registro() != null) {
            for (var campo : ctx.tipo().registro().variavel()) {
                processarDeclaracaoVariavel(campo, CategoriaSimbolos.VARIAVEL);
            }
        }
    }

    /**
     * Processa parâmetros de procedimento/função,
     * inserindo-os no escopo atual (interno).
     */
    private void processarParametro(JanderParser.ParametroContext ctx) {
        JanderParser.Tipo_estendidoContext te = ctx.tipo_estendido();
        boolean ehPonteiro = te.PONTEIRO_OP() != null;
        JanderParser.Tipo_basico_identContext tbi = te.tipo_basico_ident();

        TipoLA tipo;
        String nomeRegistro = null;

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

        for (var ident : ctx.identificador()) {
            String nome = ident.IDENT(0).getText();
            verificarEInserirSimbolo(ident.IDENT(0).getSymbol(), nome, tipo,
                    CategoriaSimbolos.VARIAVEL, nomeRegistro);
        }
    }

    /**
     * Verifica se um identificador foi declarado em algum escopo acessível.
     * Reporta erro se não encontrado.
     */
    private void verificarIdentificadorDeclarado(JanderParser.IdentificadorContext ctx) {
        String nome = ctx.IDENT(0).getText();
        if (!escopos.existeEmQualquerEscopo(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT(0).getSymbol(),
                    "identificador " + nome + " nao declarado");
        }
    }

    /**
     * Tenta inserir um símbolo no escopo atual.
     * Reporta erro se o identificador já existe no mesmo escopo (Erro 1).
     */
    private void verificarEInserirSimbolo(org.antlr.v4.runtime.Token token,
            String nome, TipoLA tipo, CategoriaSimbolos categoria,
            String nomeRegistro) {

        TabelaDeSimbolos escopoAtual = escopos.obterEscopoAtual();
        if (escopoAtual.existe(nome)) {
            // Erro 1: identificador já declarado no escopo
            AnalisadorSemanticoUtils.adicionarErroSemantico(token,
                    "identificador " + nome + " ja declarado anteriormente");
        } else {
            escopoAtual.adicionar(nome, tipo, categoria, nomeRegistro);
        }
    }

    /**
     * Obtém o TipoLA a partir de um contexto tipo_estendido da gramática.
     */
    private TipoLA obterTipoExtendido(JanderParser.Tipo_estendidoContext ctx) {
        if (ctx == null) return TipoLA.VOID;
        boolean ehPonteiro = ctx.PONTEIRO_OP() != null;
        JanderParser.Tipo_basico_identContext tbi = ctx.tipo_basico_ident();

        if (tbi.tipo_basico() != null) {
            return AnalisadorSemanticoUtils.stringParaTipo(
                    tbi.tipo_basico().getText(), ehPonteiro);
        } else {
            String nomeTipo = tbi.IDENT().getText();
            TabelaDeSimbolos.EntradaTabelaDeSimbolos entrada = escopos.verificar(nomeTipo);
            if (entrada == null) {
                AnalisadorSemanticoUtils.adicionarErroSemantico(tbi.IDENT().getSymbol(),
                        "tipo " + nomeTipo + " nao declarado");
                return TipoLA.TIPO_INDEFINIDO;
            }
            return entrada.tipo;
        }
    }

    /**
     * Obtém o TipoLA a partir de um contexto tipo da gramática.
     */
    private TipoLA obterTipoDoContexto(JanderParser.TipoContext ctx) {
        if (ctx.registro() != null) {
            return TipoLA.REGISTRO;
        }
        return obterTipoExtendido(ctx.tipo_estendido());
    }
}