// Generated from java-escape by ANTLR 4.11.1
package br.ufscar.dc.compiladores.t2;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JanderParser}.
 */
public interface JanderListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JanderParser#programa}.
	 * @param ctx the parse tree
	 */
	void enterPrograma(JanderParser.ProgramaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#programa}.
	 * @param ctx the parse tree
	 */
	void exitPrograma(JanderParser.ProgramaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#listaDeclaracoes}.
	 * @param ctx the parse tree
	 */
	void enterListaDeclaracoes(JanderParser.ListaDeclaracoesContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#listaDeclaracoes}.
	 * @param ctx the parse tree
	 */
	void exitListaDeclaracoes(JanderParser.ListaDeclaracoesContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#declaracao}.
	 * @param ctx the parse tree
	 */
	void enterDeclaracao(JanderParser.DeclaracaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#declaracao}.
	 * @param ctx the parse tree
	 */
	void exitDeclaracao(JanderParser.DeclaracaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#tipo}.
	 * @param ctx the parse tree
	 */
	void enterTipo(JanderParser.TipoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#tipo}.
	 * @param ctx the parse tree
	 */
	void exitTipo(JanderParser.TipoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#corpo}.
	 * @param ctx the parse tree
	 */
	void enterCorpo(JanderParser.CorpoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#corpo}.
	 * @param ctx the parse tree
	 */
	void exitCorpo(JanderParser.CorpoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comando}.
	 * @param ctx the parse tree
	 */
	void enterComando(JanderParser.ComandoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comando}.
	 * @param ctx the parse tree
	 */
	void exitComando(JanderParser.ComandoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comandoLeia}.
	 * @param ctx the parse tree
	 */
	void enterComandoLeia(JanderParser.ComandoLeiaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comandoLeia}.
	 * @param ctx the parse tree
	 */
	void exitComandoLeia(JanderParser.ComandoLeiaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comandoEscreva}.
	 * @param ctx the parse tree
	 */
	void enterComandoEscreva(JanderParser.ComandoEscrevaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comandoEscreva}.
	 * @param ctx the parse tree
	 */
	void exitComandoEscreva(JanderParser.ComandoEscrevaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comandoAtribuicao}.
	 * @param ctx the parse tree
	 */
	void enterComandoAtribuicao(JanderParser.ComandoAtribuicaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comandoAtribuicao}.
	 * @param ctx the parse tree
	 */
	void exitComandoAtribuicao(JanderParser.ComandoAtribuicaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comandoCondicao}.
	 * @param ctx the parse tree
	 */
	void enterComandoCondicao(JanderParser.ComandoCondicaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comandoCondicao}.
	 * @param ctx the parse tree
	 */
	void exitComandoCondicao(JanderParser.ComandoCondicaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#comandoRepeticao}.
	 * @param ctx the parse tree
	 */
	void enterComandoRepeticao(JanderParser.ComandoRepeticaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#comandoRepeticao}.
	 * @param ctx the parse tree
	 */
	void exitComandoRepeticao(JanderParser.ComandoRepeticaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#expressao}.
	 * @param ctx the parse tree
	 */
	void enterExpressao(JanderParser.ExpressaoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#expressao}.
	 * @param ctx the parse tree
	 */
	void exitExpressao(JanderParser.ExpressaoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#termoRelacional}.
	 * @param ctx the parse tree
	 */
	void enterTermoRelacional(JanderParser.TermoRelacionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#termoRelacional}.
	 * @param ctx the parse tree
	 */
	void exitTermoRelacional(JanderParser.TermoRelacionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#expressaoAritmetica}.
	 * @param ctx the parse tree
	 */
	void enterExpressaoAritmetica(JanderParser.ExpressaoAritmeticaContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#expressaoAritmetica}.
	 * @param ctx the parse tree
	 */
	void exitExpressaoAritmetica(JanderParser.ExpressaoAritmeticaContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#termoAritmetico}.
	 * @param ctx the parse tree
	 */
	void enterTermoAritmetico(JanderParser.TermoAritmeticoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#termoAritmetico}.
	 * @param ctx the parse tree
	 */
	void exitTermoAritmetico(JanderParser.TermoAritmeticoContext ctx);
	/**
	 * Enter a parse tree produced by {@link JanderParser#fatorAritmetico}.
	 * @param ctx the parse tree
	 */
	void enterFatorAritmetico(JanderParser.FatorAritmeticoContext ctx);
	/**
	 * Exit a parse tree produced by {@link JanderParser#fatorAritmetico}.
	 * @param ctx the parse tree
	 */
	void exitFatorAritmetico(JanderParser.FatorAritmeticoContext ctx);
}