// Generated from java-escape by ANTLR 4.11.1
package br.ufscar.dc.compiladores.t2;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class JanderParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ALGORITMO=1, DECLARE=2, LEIA=3, ESCREVA=4, FIM_ALGORITMO=5, SE=6, ENTAO=7, 
		SENAO=8, FIM_SE=9, PARA=10, FIM_PARA=11, ENQUANTO=12, FIM_ENQUANTO=13, 
		FACA=14, ATE=15, RETORNE=16, INTEIRO=17, REAL=18, LITERAL=19, LOGICO=20, 
		VERDADEIRO=21, FALSO=22, OP_ARIT_SOMA=23, OP_ARIT_MULT=24, OP_RELAC=25, 
		OP_BOOL_E=26, OP_BOOL_OU=27, OP_BOOL_NAO=28, ATRIB=29, DELIM=30, VIRGULA=31, 
		ABREPAR=32, FECHAPAR=33, PONTO=34, IDENT=35, NUM_REAL=36, NUM_INT=37, 
		CADEIA=38, WS=39, COMENTARIO=40, CADEIA_NAO_FECHADA=41, INVALIDO=42;
	public static final int
		RULE_programa = 0, RULE_listaDeclaracoes = 1, RULE_declaracao = 2, RULE_tipo = 3, 
		RULE_corpo = 4, RULE_comando = 5, RULE_comandoLeia = 6, RULE_comandoEscreva = 7, 
		RULE_comandoAtribuicao = 8, RULE_comandoCondicao = 9, RULE_comandoRepeticao = 10, 
		RULE_expressao = 11, RULE_termoRelacional = 12, RULE_expressaoAritmetica = 13, 
		RULE_termoAritmetico = 14, RULE_fatorAritmetico = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"programa", "listaDeclaracoes", "declaracao", "tipo", "corpo", "comando", 
			"comandoLeia", "comandoEscreva", "comandoAtribuicao", "comandoCondicao", 
			"comandoRepeticao", "expressao", "termoRelacional", "expressaoAritmetica", 
			"termoAritmetico", "fatorAritmetico"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'algoritmo'", "'declare'", "'leia'", "'escreva'", "'fim_algoritmo'", 
			"'se'", "'entao'", "'senao'", "'fim_se'", "'para'", "'fim_para'", "'enquanto'", 
			"'fim_enquanto'", "'faca'", "'ate'", "'retorne'", "'inteiro'", "'real'", 
			"'literal'", "'logico'", "'verdadeiro'", "'falso'", null, null, null, 
			"'e'", "'ou'", "'nao'", "'<-'", "':'", "','", "'('", "')'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ALGORITMO", "DECLARE", "LEIA", "ESCREVA", "FIM_ALGORITMO", "SE", 
			"ENTAO", "SENAO", "FIM_SE", "PARA", "FIM_PARA", "ENQUANTO", "FIM_ENQUANTO", 
			"FACA", "ATE", "RETORNE", "INTEIRO", "REAL", "LITERAL", "LOGICO", "VERDADEIRO", 
			"FALSO", "OP_ARIT_SOMA", "OP_ARIT_MULT", "OP_RELAC", "OP_BOOL_E", "OP_BOOL_OU", 
			"OP_BOOL_NAO", "ATRIB", "DELIM", "VIRGULA", "ABREPAR", "FECHAPAR", "PONTO", 
			"IDENT", "NUM_REAL", "NUM_INT", "CADEIA", "WS", "COMENTARIO", "CADEIA_NAO_FECHADA", 
			"INVALIDO"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JanderParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramaContext extends ParserRuleContext {
		public TerminalNode ALGORITMO() { return getToken(JanderParser.ALGORITMO, 0); }
		public ListaDeclaracoesContext listaDeclaracoes() {
			return getRuleContext(ListaDeclaracoesContext.class,0);
		}
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_ALGORITMO() { return getToken(JanderParser.FIM_ALGORITMO, 0); }
		public ProgramaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programa; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterPrograma(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitPrograma(this);
		}
	}

	public final ProgramaContext programa() throws RecognitionException {
		ProgramaContext _localctx = new ProgramaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_programa);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(ALGORITMO);
			setState(33);
			listaDeclaracoes();
			setState(34);
			corpo();
			setState(35);
			match(FIM_ALGORITMO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ListaDeclaracoesContext extends ParserRuleContext {
		public TerminalNode DECLARE() { return getToken(JanderParser.DECLARE, 0); }
		public List<DeclaracaoContext> declaracao() {
			return getRuleContexts(DeclaracaoContext.class);
		}
		public DeclaracaoContext declaracao(int i) {
			return getRuleContext(DeclaracaoContext.class,i);
		}
		public ListaDeclaracoesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listaDeclaracoes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterListaDeclaracoes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitListaDeclaracoes(this);
		}
	}

	public final ListaDeclaracoesContext listaDeclaracoes() throws RecognitionException {
		ListaDeclaracoesContext _localctx = new ListaDeclaracoesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_listaDeclaracoes);
		try {
			int _alt;
			setState(44);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DECLARE:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				match(DECLARE);
				setState(39); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(38);
						declaracao();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(41); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case LEIA:
			case ESCREVA:
			case FIM_ALGORITMO:
			case SE:
			case ENQUANTO:
			case IDENT:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracaoContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(JanderParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(JanderParser.IDENT, i);
		}
		public TerminalNode DELIM() { return getToken(JanderParser.DELIM, 0); }
		public TipoContext tipo() {
			return getRuleContext(TipoContext.class,0);
		}
		public List<TerminalNode> VIRGULA() { return getTokens(JanderParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(JanderParser.VIRGULA, i);
		}
		public DeclaracaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracao; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterDeclaracao(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitDeclaracao(this);
		}
	}

	public final DeclaracaoContext declaracao() throws RecognitionException {
		DeclaracaoContext _localctx = new DeclaracaoContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaracao);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(IDENT);
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(47);
				match(VIRGULA);
				setState(48);
				match(IDENT);
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(54);
			match(DELIM);
			setState(55);
			tipo();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TipoContext extends ParserRuleContext {
		public TerminalNode INTEIRO() { return getToken(JanderParser.INTEIRO, 0); }
		public TerminalNode REAL() { return getToken(JanderParser.REAL, 0); }
		public TerminalNode LITERAL() { return getToken(JanderParser.LITERAL, 0); }
		public TerminalNode LOGICO() { return getToken(JanderParser.LOGICO, 0); }
		public TipoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterTipo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitTipo(this);
		}
	}

	public final TipoContext tipo() throws RecognitionException {
		TipoContext _localctx = new TipoContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_tipo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CorpoContext extends ParserRuleContext {
		public List<ComandoContext> comando() {
			return getRuleContexts(ComandoContext.class);
		}
		public ComandoContext comando(int i) {
			return getRuleContext(ComandoContext.class,i);
		}
		public CorpoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_corpo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterCorpo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitCorpo(this);
		}
	}

	public final CorpoContext corpo() throws RecognitionException {
		CorpoContext _localctx = new CorpoContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_corpo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((_la) & ~0x3f) == 0 && ((1L << _la) & 34359742552L) != 0) {
				{
				{
				setState(59);
				comando();
				}
				}
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoContext extends ParserRuleContext {
		public ComandoLeiaContext comandoLeia() {
			return getRuleContext(ComandoLeiaContext.class,0);
		}
		public ComandoEscrevaContext comandoEscreva() {
			return getRuleContext(ComandoEscrevaContext.class,0);
		}
		public ComandoAtribuicaoContext comandoAtribuicao() {
			return getRuleContext(ComandoAtribuicaoContext.class,0);
		}
		public ComandoCondicaoContext comandoCondicao() {
			return getRuleContext(ComandoCondicaoContext.class,0);
		}
		public ComandoRepeticaoContext comandoRepeticao() {
			return getRuleContext(ComandoRepeticaoContext.class,0);
		}
		public ComandoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comando; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComando(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComando(this);
		}
	}

	public final ComandoContext comando() throws RecognitionException {
		ComandoContext _localctx = new ComandoContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_comando);
		try {
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LEIA:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				comandoLeia();
				}
				break;
			case ESCREVA:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				comandoEscreva();
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(67);
				comandoAtribuicao();
				}
				break;
			case SE:
				enterOuterAlt(_localctx, 4);
				{
				setState(68);
				comandoCondicao();
				}
				break;
			case ENQUANTO:
				enterOuterAlt(_localctx, 5);
				{
				setState(69);
				comandoRepeticao();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoLeiaContext extends ParserRuleContext {
		public TerminalNode LEIA() { return getToken(JanderParser.LEIA, 0); }
		public TerminalNode ABREPAR() { return getToken(JanderParser.ABREPAR, 0); }
		public List<TerminalNode> IDENT() { return getTokens(JanderParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(JanderParser.IDENT, i);
		}
		public TerminalNode FECHAPAR() { return getToken(JanderParser.FECHAPAR, 0); }
		public List<TerminalNode> VIRGULA() { return getTokens(JanderParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(JanderParser.VIRGULA, i);
		}
		public ComandoLeiaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comandoLeia; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComandoLeia(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComandoLeia(this);
		}
	}

	public final ComandoLeiaContext comandoLeia() throws RecognitionException {
		ComandoLeiaContext _localctx = new ComandoLeiaContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_comandoLeia);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(LEIA);
			setState(73);
			match(ABREPAR);
			setState(74);
			match(IDENT);
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(75);
				match(VIRGULA);
				setState(76);
				match(IDENT);
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(82);
			match(FECHAPAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoEscrevaContext extends ParserRuleContext {
		public TerminalNode ESCREVA() { return getToken(JanderParser.ESCREVA, 0); }
		public TerminalNode ABREPAR() { return getToken(JanderParser.ABREPAR, 0); }
		public List<ExpressaoContext> expressao() {
			return getRuleContexts(ExpressaoContext.class);
		}
		public ExpressaoContext expressao(int i) {
			return getRuleContext(ExpressaoContext.class,i);
		}
		public TerminalNode FECHAPAR() { return getToken(JanderParser.FECHAPAR, 0); }
		public List<TerminalNode> VIRGULA() { return getTokens(JanderParser.VIRGULA); }
		public TerminalNode VIRGULA(int i) {
			return getToken(JanderParser.VIRGULA, i);
		}
		public ComandoEscrevaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comandoEscreva; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComandoEscreva(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComandoEscreva(this);
		}
	}

	public final ComandoEscrevaContext comandoEscreva() throws RecognitionException {
		ComandoEscrevaContext _localctx = new ComandoEscrevaContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_comandoEscreva);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(ESCREVA);
			setState(85);
			match(ABREPAR);
			setState(86);
			expressao();
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VIRGULA) {
				{
				{
				setState(87);
				match(VIRGULA);
				setState(88);
				expressao();
				}
				}
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(94);
			match(FECHAPAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoAtribuicaoContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(JanderParser.IDENT, 0); }
		public TerminalNode ATRIB() { return getToken(JanderParser.ATRIB, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public ComandoAtribuicaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comandoAtribuicao; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComandoAtribuicao(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComandoAtribuicao(this);
		}
	}

	public final ComandoAtribuicaoContext comandoAtribuicao() throws RecognitionException {
		ComandoAtribuicaoContext _localctx = new ComandoAtribuicaoContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_comandoAtribuicao);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(IDENT);
			setState(97);
			match(ATRIB);
			setState(98);
			expressao();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoCondicaoContext extends ParserRuleContext {
		public TerminalNode SE() { return getToken(JanderParser.SE, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode ENTAO() { return getToken(JanderParser.ENTAO, 0); }
		public List<CorpoContext> corpo() {
			return getRuleContexts(CorpoContext.class);
		}
		public CorpoContext corpo(int i) {
			return getRuleContext(CorpoContext.class,i);
		}
		public TerminalNode FIM_SE() { return getToken(JanderParser.FIM_SE, 0); }
		public TerminalNode SENAO() { return getToken(JanderParser.SENAO, 0); }
		public ComandoCondicaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comandoCondicao; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComandoCondicao(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComandoCondicao(this);
		}
	}

	public final ComandoCondicaoContext comandoCondicao() throws RecognitionException {
		ComandoCondicaoContext _localctx = new ComandoCondicaoContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_comandoCondicao);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			match(SE);
			setState(101);
			expressao();
			setState(102);
			match(ENTAO);
			setState(103);
			corpo();
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SENAO) {
				{
				setState(104);
				match(SENAO);
				setState(105);
				corpo();
				}
			}

			setState(108);
			match(FIM_SE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComandoRepeticaoContext extends ParserRuleContext {
		public TerminalNode ENQUANTO() { return getToken(JanderParser.ENQUANTO, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode FACA() { return getToken(JanderParser.FACA, 0); }
		public CorpoContext corpo() {
			return getRuleContext(CorpoContext.class,0);
		}
		public TerminalNode FIM_ENQUANTO() { return getToken(JanderParser.FIM_ENQUANTO, 0); }
		public ComandoRepeticaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comandoRepeticao; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterComandoRepeticao(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitComandoRepeticao(this);
		}
	}

	public final ComandoRepeticaoContext comandoRepeticao() throws RecognitionException {
		ComandoRepeticaoContext _localctx = new ComandoRepeticaoContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_comandoRepeticao);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(ENQUANTO);
			setState(111);
			expressao();
			setState(112);
			match(FACA);
			setState(113);
			corpo();
			setState(114);
			match(FIM_ENQUANTO);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressaoContext extends ParserRuleContext {
		public List<TermoRelacionalContext> termoRelacional() {
			return getRuleContexts(TermoRelacionalContext.class);
		}
		public TermoRelacionalContext termoRelacional(int i) {
			return getRuleContext(TermoRelacionalContext.class,i);
		}
		public List<TerminalNode> OP_BOOL_OU() { return getTokens(JanderParser.OP_BOOL_OU); }
		public TerminalNode OP_BOOL_OU(int i) {
			return getToken(JanderParser.OP_BOOL_OU, i);
		}
		public ExpressaoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressao; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterExpressao(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitExpressao(this);
		}
	}

	public final ExpressaoContext expressao() throws RecognitionException {
		ExpressaoContext _localctx = new ExpressaoContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_expressao);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			termoRelacional();
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OP_BOOL_OU) {
				{
				{
				setState(117);
				match(OP_BOOL_OU);
				setState(118);
				termoRelacional();
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TermoRelacionalContext extends ParserRuleContext {
		public List<ExpressaoAritmeticaContext> expressaoAritmetica() {
			return getRuleContexts(ExpressaoAritmeticaContext.class);
		}
		public ExpressaoAritmeticaContext expressaoAritmetica(int i) {
			return getRuleContext(ExpressaoAritmeticaContext.class,i);
		}
		public TerminalNode OP_RELAC() { return getToken(JanderParser.OP_RELAC, 0); }
		public TermoRelacionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termoRelacional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterTermoRelacional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitTermoRelacional(this);
		}
	}

	public final TermoRelacionalContext termoRelacional() throws RecognitionException {
		TermoRelacionalContext _localctx = new TermoRelacionalContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_termoRelacional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			expressaoAritmetica();
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OP_RELAC) {
				{
				setState(125);
				match(OP_RELAC);
				setState(126);
				expressaoAritmetica();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressaoAritmeticaContext extends ParserRuleContext {
		public List<TermoAritmeticoContext> termoAritmetico() {
			return getRuleContexts(TermoAritmeticoContext.class);
		}
		public TermoAritmeticoContext termoAritmetico(int i) {
			return getRuleContext(TermoAritmeticoContext.class,i);
		}
		public List<TerminalNode> OP_ARIT_SOMA() { return getTokens(JanderParser.OP_ARIT_SOMA); }
		public TerminalNode OP_ARIT_SOMA(int i) {
			return getToken(JanderParser.OP_ARIT_SOMA, i);
		}
		public ExpressaoAritmeticaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressaoAritmetica; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterExpressaoAritmetica(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitExpressaoAritmetica(this);
		}
	}

	public final ExpressaoAritmeticaContext expressaoAritmetica() throws RecognitionException {
		ExpressaoAritmeticaContext _localctx = new ExpressaoAritmeticaContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_expressaoAritmetica);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			termoAritmetico();
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OP_ARIT_SOMA) {
				{
				{
				setState(130);
				match(OP_ARIT_SOMA);
				setState(131);
				termoAritmetico();
				}
				}
				setState(136);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TermoAritmeticoContext extends ParserRuleContext {
		public List<FatorAritmeticoContext> fatorAritmetico() {
			return getRuleContexts(FatorAritmeticoContext.class);
		}
		public FatorAritmeticoContext fatorAritmetico(int i) {
			return getRuleContext(FatorAritmeticoContext.class,i);
		}
		public List<TerminalNode> OP_ARIT_MULT() { return getTokens(JanderParser.OP_ARIT_MULT); }
		public TerminalNode OP_ARIT_MULT(int i) {
			return getToken(JanderParser.OP_ARIT_MULT, i);
		}
		public TermoAritmeticoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termoAritmetico; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterTermoAritmetico(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitTermoAritmetico(this);
		}
	}

	public final TermoAritmeticoContext termoAritmetico() throws RecognitionException {
		TermoAritmeticoContext _localctx = new TermoAritmeticoContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_termoAritmetico);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			fatorAritmetico();
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OP_ARIT_MULT) {
				{
				{
				setState(138);
				match(OP_ARIT_MULT);
				setState(139);
				fatorAritmetico();
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FatorAritmeticoContext extends ParserRuleContext {
		public TerminalNode NUM_INT() { return getToken(JanderParser.NUM_INT, 0); }
		public TerminalNode NUM_REAL() { return getToken(JanderParser.NUM_REAL, 0); }
		public TerminalNode IDENT() { return getToken(JanderParser.IDENT, 0); }
		public TerminalNode CADEIA() { return getToken(JanderParser.CADEIA, 0); }
		public TerminalNode VERDADEIRO() { return getToken(JanderParser.VERDADEIRO, 0); }
		public TerminalNode FALSO() { return getToken(JanderParser.FALSO, 0); }
		public TerminalNode ABREPAR() { return getToken(JanderParser.ABREPAR, 0); }
		public ExpressaoContext expressao() {
			return getRuleContext(ExpressaoContext.class,0);
		}
		public TerminalNode FECHAPAR() { return getToken(JanderParser.FECHAPAR, 0); }
		public FatorAritmeticoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fatorAritmetico; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).enterFatorAritmetico(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JanderListener ) ((JanderListener)listener).exitFatorAritmetico(this);
		}
	}

	public final FatorAritmeticoContext fatorAritmetico() throws RecognitionException {
		FatorAritmeticoContext _localctx = new FatorAritmeticoContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_fatorAritmetico);
		try {
			setState(155);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NUM_INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				match(NUM_INT);
				}
				break;
			case NUM_REAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				match(NUM_REAL);
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(147);
				match(IDENT);
				}
				break;
			case CADEIA:
				enterOuterAlt(_localctx, 4);
				{
				setState(148);
				match(CADEIA);
				}
				break;
			case VERDADEIRO:
				enterOuterAlt(_localctx, 5);
				{
				setState(149);
				match(VERDADEIRO);
				}
				break;
			case FALSO:
				enterOuterAlt(_localctx, 6);
				{
				setState(150);
				match(FALSO);
				}
				break;
			case ABREPAR:
				enterOuterAlt(_localctx, 7);
				{
				setState(151);
				match(ABREPAR);
				setState(152);
				expressao();
				setState(153);
				match(FECHAPAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001*\u009e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0004\u0001(\b\u0001\u000b\u0001\f\u0001)\u0001\u0001\u0003"+
		"\u0001-\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u00022\b\u0002"+
		"\n\u0002\f\u00025\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0005\u0004=\b\u0004\n\u0004\f\u0004@\t\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005"+
		"G\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0005\u0006N\b\u0006\n\u0006\f\u0006Q\t\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007"+
		"Z\b\u0007\n\u0007\f\u0007]\t\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003"+
		"\tk\b\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000bx\b\u000b\n\u000b\f"+
		"\u000b{\t\u000b\u0001\f\u0001\f\u0001\f\u0003\f\u0080\b\f\u0001\r\u0001"+
		"\r\u0001\r\u0005\r\u0085\b\r\n\r\f\r\u0088\t\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0005\u000e\u008d\b\u000e\n\u000e\f\u000e\u0090\t\u000e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u009c\b\u000f\u0001"+
		"\u000f\u0000\u0000\u0010\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e\u0000\u0001\u0001\u0000\u0011\u0014"+
		"\u00a2\u0000 \u0001\u0000\u0000\u0000\u0002,\u0001\u0000\u0000\u0000\u0004"+
		".\u0001\u0000\u0000\u0000\u00069\u0001\u0000\u0000\u0000\b>\u0001\u0000"+
		"\u0000\u0000\nF\u0001\u0000\u0000\u0000\fH\u0001\u0000\u0000\u0000\u000e"+
		"T\u0001\u0000\u0000\u0000\u0010`\u0001\u0000\u0000\u0000\u0012d\u0001"+
		"\u0000\u0000\u0000\u0014n\u0001\u0000\u0000\u0000\u0016t\u0001\u0000\u0000"+
		"\u0000\u0018|\u0001\u0000\u0000\u0000\u001a\u0081\u0001\u0000\u0000\u0000"+
		"\u001c\u0089\u0001\u0000\u0000\u0000\u001e\u009b\u0001\u0000\u0000\u0000"+
		" !\u0005\u0001\u0000\u0000!\"\u0003\u0002\u0001\u0000\"#\u0003\b\u0004"+
		"\u0000#$\u0005\u0005\u0000\u0000$\u0001\u0001\u0000\u0000\u0000%\'\u0005"+
		"\u0002\u0000\u0000&(\u0003\u0004\u0002\u0000\'&\u0001\u0000\u0000\u0000"+
		"()\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000"+
		"\u0000*-\u0001\u0000\u0000\u0000+-\u0001\u0000\u0000\u0000,%\u0001\u0000"+
		"\u0000\u0000,+\u0001\u0000\u0000\u0000-\u0003\u0001\u0000\u0000\u0000"+
		".3\u0005#\u0000\u0000/0\u0005\u001f\u0000\u000002\u0005#\u0000\u00001"+
		"/\u0001\u0000\u0000\u000025\u0001\u0000\u0000\u000031\u0001\u0000\u0000"+
		"\u000034\u0001\u0000\u0000\u000046\u0001\u0000\u0000\u000053\u0001\u0000"+
		"\u0000\u000067\u0005\u001e\u0000\u000078\u0003\u0006\u0003\u00008\u0005"+
		"\u0001\u0000\u0000\u00009:\u0007\u0000\u0000\u0000:\u0007\u0001\u0000"+
		"\u0000\u0000;=\u0003\n\u0005\u0000<;\u0001\u0000\u0000\u0000=@\u0001\u0000"+
		"\u0000\u0000><\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?\t\u0001"+
		"\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000AG\u0003\f\u0006\u0000BG\u0003"+
		"\u000e\u0007\u0000CG\u0003\u0010\b\u0000DG\u0003\u0012\t\u0000EG\u0003"+
		"\u0014\n\u0000FA\u0001\u0000\u0000\u0000FB\u0001\u0000\u0000\u0000FC\u0001"+
		"\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FE\u0001\u0000\u0000\u0000"+
		"G\u000b\u0001\u0000\u0000\u0000HI\u0005\u0003\u0000\u0000IJ\u0005 \u0000"+
		"\u0000JO\u0005#\u0000\u0000KL\u0005\u001f\u0000\u0000LN\u0005#\u0000\u0000"+
		"MK\u0001\u0000\u0000\u0000NQ\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000"+
		"\u0000OP\u0001\u0000\u0000\u0000PR\u0001\u0000\u0000\u0000QO\u0001\u0000"+
		"\u0000\u0000RS\u0005!\u0000\u0000S\r\u0001\u0000\u0000\u0000TU\u0005\u0004"+
		"\u0000\u0000UV\u0005 \u0000\u0000V[\u0003\u0016\u000b\u0000WX\u0005\u001f"+
		"\u0000\u0000XZ\u0003\u0016\u000b\u0000YW\u0001\u0000\u0000\u0000Z]\u0001"+
		"\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000"+
		"\\^\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000^_\u0005!\u0000\u0000"+
		"_\u000f\u0001\u0000\u0000\u0000`a\u0005#\u0000\u0000ab\u0005\u001d\u0000"+
		"\u0000bc\u0003\u0016\u000b\u0000c\u0011\u0001\u0000\u0000\u0000de\u0005"+
		"\u0006\u0000\u0000ef\u0003\u0016\u000b\u0000fg\u0005\u0007\u0000\u0000"+
		"gj\u0003\b\u0004\u0000hi\u0005\b\u0000\u0000ik\u0003\b\u0004\u0000jh\u0001"+
		"\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000"+
		"lm\u0005\t\u0000\u0000m\u0013\u0001\u0000\u0000\u0000no\u0005\f\u0000"+
		"\u0000op\u0003\u0016\u000b\u0000pq\u0005\u000e\u0000\u0000qr\u0003\b\u0004"+
		"\u0000rs\u0005\r\u0000\u0000s\u0015\u0001\u0000\u0000\u0000ty\u0003\u0018"+
		"\f\u0000uv\u0005\u001b\u0000\u0000vx\u0003\u0018\f\u0000wu\u0001\u0000"+
		"\u0000\u0000x{\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000yz\u0001"+
		"\u0000\u0000\u0000z\u0017\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000"+
		"\u0000|\u007f\u0003\u001a\r\u0000}~\u0005\u0019\u0000\u0000~\u0080\u0003"+
		"\u001a\r\u0000\u007f}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000"+
		"\u0000\u0000\u0080\u0019\u0001\u0000\u0000\u0000\u0081\u0086\u0003\u001c"+
		"\u000e\u0000\u0082\u0083\u0005\u0017\u0000\u0000\u0083\u0085\u0003\u001c"+
		"\u000e\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000"+
		"\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000"+
		"\u0000\u0000\u0087\u001b\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000"+
		"\u0000\u0000\u0089\u008e\u0003\u001e\u000f\u0000\u008a\u008b\u0005\u0018"+
		"\u0000\u0000\u008b\u008d\u0003\u001e\u000f\u0000\u008c\u008a\u0001\u0000"+
		"\u0000\u0000\u008d\u0090\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000"+
		"\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u001d\u0001\u0000"+
		"\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0091\u009c\u0005%\u0000"+
		"\u0000\u0092\u009c\u0005$\u0000\u0000\u0093\u009c\u0005#\u0000\u0000\u0094"+
		"\u009c\u0005&\u0000\u0000\u0095\u009c\u0005\u0015\u0000\u0000\u0096\u009c"+
		"\u0005\u0016\u0000\u0000\u0097\u0098\u0005 \u0000\u0000\u0098\u0099\u0003"+
		"\u0016\u000b\u0000\u0099\u009a\u0005!\u0000\u0000\u009a\u009c\u0001\u0000"+
		"\u0000\u0000\u009b\u0091\u0001\u0000\u0000\u0000\u009b\u0092\u0001\u0000"+
		"\u0000\u0000\u009b\u0093\u0001\u0000\u0000\u0000\u009b\u0094\u0001\u0000"+
		"\u0000\u0000\u009b\u0095\u0001\u0000\u0000\u0000\u009b\u0096\u0001\u0000"+
		"\u0000\u0000\u009b\u0097\u0001\u0000\u0000\u0000\u009c\u001f\u0001\u0000"+
		"\u0000\u0000\r),3>FO[jy\u007f\u0086\u008e\u009b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}