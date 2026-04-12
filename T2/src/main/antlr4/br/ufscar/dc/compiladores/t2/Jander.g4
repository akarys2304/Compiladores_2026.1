grammar Jander;

// LÉXICO

PROCEDIMENTO : 'procedimento';
FUNCAO : 'funcao';
RETORNE : 'retorne';
ALGORITMO : 'algoritmo';
DECLARE : 'declare';
CONSTANTE : 'constante';
LEIA : 'leia';
ESCREVA : 'escreva';
FIM_ALGORITMO : 'fim_algoritmo';
FIM_PROCEDIMENTO : 'fim_procedimento';
FIM_FUNCAO : 'fim_funcao';

SE : 'se';
ENTAO : 'entao';
SENAO : 'senao';
FIM_SE : 'fim_se';

CASO : 'caso';
SEJA : 'seja';
FIM_CASO : 'fim_caso';

ENQUANTO : 'enquanto';
FIM_ENQUANTO : 'fim_enquanto';
FACA : 'faca';
PARA : 'para';
ATE : 'ate';
FIM_PARA : 'fim_para';

INTEIRO : 'inteiro';
REAL : 'real';
LITERAL : 'literal';
LOGICO : 'logico';
VERDADEIRO : 'verdadeiro';
FALSO : 'falso';

TIPO : 'tipo';
REGISTRO : 'registro';
FIM_REGISTRO : 'fim_registro';
VAR : 'var';

ATRIB : '<-';
DELIM : ':';
VIRGULA : ',';
PONTO : '.';
IGUAL : '=';
ABREPAR : '(';
FECHAPAR : ')';
ABRECOLCH : '[';
FECHACOLCH : ']';
PONTEIRO_OP : '^';
ENDERECO_OP : '&';
PONTOS_INTERVALO : '..';

OP_ARIT_SOMA : '+' | '-';
OP_ARIT_MULT : '*' | '/';
OP_ARIT_MOD : '%';
OP_RELAC : '<>' | '>=' | '<=' | '>' | '<';
OP_BOOL_E : 'e';
OP_BOOL_OU : 'ou';
OP_BOOL_NAO : 'nao';

IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;
NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT : [0-9]+ ;

CADEIA : '"' (~["\\\r\n] | ESC_SEQ)* '"' ;
CADEIA_NAO_FECHADA : '"' (~["\\\r\n] | ESC_SEQ)* ;
fragment ESC_SEQ : '\\' . ;

WS : [ \t\r\n]+ -> skip ;
COMENTARIO : '{' ~('}'|'\n')* '}' -> skip ;
COMENTARIO_NAO_FECHADO : '{' ~'}'* ;

ERRO : . ;

// SINTÁTICO

programa : declaracoes ALGORITMO corpo FIM_ALGORITMO EOF ;

declaracoes : decl_local_global* ;

decl_local_global : declaracao_local | declaracao_global ;

declaracao_local : DECLARE variavel | CONSTANTE IDENT DELIM tipo_basico IGUAL valor_constante | TIPO IDENT DELIM tipo ;

variavel : identificador (VIRGULA identificador)* DELIM tipo ;

identificador : PONTEIRO_OP? IDENT (PONTO IDENT)* dimensao ;

dimensao : (ABRECOLCH exp_aritmetica FECHACOLCH)* ;

tipo : registro | tipo_estendido ;

tipo_basico : LITERAL | INTEIRO | REAL | LOGICO ;

tipo_basico_ident : tipo_basico | IDENT ;

tipo_estendido : PONTEIRO_OP? tipo_basico_ident ;

valor_constante : CADEIA | NUM_INT | NUM_REAL | VERDADEIRO | FALSO ;

registro : REGISTRO variavel* FIM_REGISTRO ;

declaracao_global : procedimento | funcao ;

procedimento : PROCEDIMENTO IDENT ABREPAR parametros? FECHAPAR declaracao_local* cmd* FIM_PROCEDIMENTO ;

funcao : FUNCAO IDENT ABREPAR parametros? FECHAPAR DELIM tipo_estendido declaracao_local* cmd* FIM_FUNCAO ;

parametros : parametro (VIRGULA parametro)* ;

parametro : VAR? identificador (VIRGULA identificador)* DELIM tipo_estendido ;

corpo : declaracao_local* cmd* ;

cmd : cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca | cmdAtribuicao | cmdChamada | cmdRetorne ;

cmdLeia : LEIA ABREPAR PONTEIRO_OP? identificador (VIRGULA PONTEIRO_OP? identificador)* FECHAPAR ;

cmdEscreva : ESCREVA ABREPAR expressao (VIRGULA expressao)* FECHAPAR ;

cmdSe : SE expressao ENTAO cmd* (SENAO cmd*)? FIM_SE ;

cmdCaso : CASO exp_aritmetica SEJA selecao (SENAO cmd*)? FIM_CASO ;

selecao : item_selecao+ ;

item_selecao : constantes DELIM cmd* ;

constantes : numero_intervalo (VIRGULA numero_intervalo)* ;

numero_intervalo : OP_ARIT_SOMA? NUM_INT (PONTOS_INTERVALO OP_ARIT_SOMA? NUM_INT)? ;

cmdPara : PARA IDENT ATRIB exp_aritmetica ATE exp_aritmetica FACA cmd* FIM_PARA ;

cmdEnquanto : ENQUANTO expressao FACA cmd* FIM_ENQUANTO ;

cmdFaca : FACA cmd* ATE expressao ;

cmdAtribuicao : PONTEIRO_OP? identificador ATRIB expressao ;

cmdChamada : IDENT ABREPAR (expressao (VIRGULA expressao)*)? FECHAPAR ;

cmdRetorne : RETORNE expressao ;

// EXPRESSÕES

expressao : termo_logico (OP_BOOL_OU termo_logico)* ;

termo_logico : fator_logico (OP_BOOL_E fator_logico)* ;

fator_logico : OP_BOOL_NAO? parcela_logica ;

parcela_logica : VERDADEIRO | FALSO | exp_relacional ;

exp_relacional : exp_aritmetica ((OP_RELAC | IGUAL) exp_aritmetica)? ;

exp_aritmetica : termo (OP_ARIT_SOMA termo)* ;

termo : fator (OP_ARIT_MULT fator)* ;

fator : parcela (OP_ARIT_MOD parcela)* ;

parcela : OP_ARIT_SOMA? parcela_unario | parcela_nao_unario ;

parcela_unario : PONTEIRO_OP? identificador | IDENT ABREPAR (expressao (VIRGULA expressao)*)? FECHAPAR | NUM_INT | NUM_REAL | ABREPAR expressao FECHAPAR ;

parcela_nao_unario : ENDERECO_OP identificador | CADEIA ;