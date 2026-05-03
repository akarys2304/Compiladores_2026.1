package br.ufscar.dc.compiladores.t3;

import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.CategoriaSimbolos;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.TipoLA;


// Classe responsável por percorrer a árvore sintática e validar as regras semânticas
public class AnalisadorSemantico extends JanderBaseVisitor<Void> {

    private Escopos escopos;

    // Inicializa o gerenciador de escopos ao iniciar a visita ao programa
    @Override
    public Void visitPrograma(JanderParser.ProgramaContext ctx) {
        escopos = new Escopos();
        return super.visitPrograma(ctx);
    }

    // Gerencia a declaração de variáveis, constantes e novos tipos (registros)
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
            // Define um novo tipo customizado na tabela de símbolos
            String nome = ctx.IDENT().getText();
            TipoLA tipo = obterTipoDoContexto(ctx.tipo());
            verificarEInserirSimbolo(ctx.IDENT().getSymbol(), nome, tipo,
                    CategoriaSimbolos.TIPO, nome);
        }
        return super.visitDeclaracao_local(ctx);
    }

   // Valida se as variáveis passadas para leitura (leia) foram devidamente declaradas
    @Override
    public Void visitCmdLeia(JanderParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            verificarIdentificadorDeclarado(ident);
        }
        return super.visitCmdLeia(ctx);
    }

    // Verifica a consistência dos tipos das expressões dentro de um comando de escrita
    @Override
    public Void visitCmdEscreva(JanderParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            AnalisadorSemanticoUtils.verificarTipo(escopos, expr);
        }
        return super.visitCmdEscreva(ctx);
    }

    // Verifica se o identificador existe e se o tipo da expressão é compatível com o da variável
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
            TipoLA tipoExpr = AnalisadorSemanticoUtils.verificarTipo(escopos, ctx.expressao());
            // Dispara erro se tentar atribuir, por exemplo, um literal a um inteiro
            if (!AnalisadorSemanticoUtils.tiposCompativeis(tipoVar, tipoExpr)) {
                AnalisadorSemanticoUtils.adicionarErroSemantico(
                        ctx.identificador().IDENT(0).getSymbol(),
                        "atribuicao nao compativel para " + nome);
            }
        }
        return super.visitCmdAtribuicao(ctx);
    }

    // Valida os tipos em estruturas de controle (condições devem ser lógicas)
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

    // Lógica auxiliar para decompor a declaração de variáveis e identificar tipos/ponteiros
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
                // Caso a variável use um tipo customizado já declarado anteriormente
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

        // Insere cada identificador da lista (ex: a, b, c : inteiro) na tabela
        for (var ident : ctx.identificador()) {
            String nome = ident.IDENT(0).getText();
            verificarEInserirSimbolo(ident.IDENT(0).getSymbol(), nome, tipo,
                    categoria, nomeRegistro);
        }
    }

    // Verifica se um nome existe em qualquer nível de escopo (local ou global)
    private void verificarIdentificadorDeclarado(JanderParser.IdentificadorContext ctx) {
        String nome = ctx.IDENT(0).getText();
        if (!escopos.existeEmQualquerEscopo(nome)) {
            AnalisadorSemanticoUtils.adicionarErroSemantico(ctx.IDENT(0).getSymbol(),
                    "identificador " + nome + " nao declarado");
        }
    }

    // Centraliza a lógica de inserção para evitar duplicidade de nomes no mesmo escopo
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

    // Converte o contexto da gramática para o Enum interno de tipos
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