grammar Jander;

// REGRAS LÉXICAS (TOKENS)

// Palavras-chave transformadas em tokens individuais
ALGORITMO    : 'algoritmo';
DECLARE      : 'declare';
LEIA         : 'leia';
ESCREVA      : 'escreva';
FIM_ALGORITMO: 'fim_algoritmo';
SE           : 'se';
ENTAO        : 'entao';
SENAO        : 'senao';
FIM_SE       : 'fim_se';
PARA         : 'para';
FIM_PARA     : 'fim_para';
ENQUANTO     : 'enquanto';
FIM_ENQUANTO : 'fim_enquanto';
FACA         : 'faca';
ATE          : 'ate';
RETORNE      : 'retorne';
INTEIRO      : 'inteiro';
REAL         : 'real';
LITERAL      : 'literal';
LOGICO       : 'logico';
VERDADEIRO   : 'verdadeiro';
FALSO        : 'falso';

// Operadores
OP_ARIT_SOMA : '+' | '-';
OP_ARIT_MULT : '*' | '/' | '%';
OP_RELAC     : '>' | '<' | '>=' | '<=' | '<>' | '=';
OP_BOOL_E    : 'e';
OP_BOOL_OU   : 'ou';
OP_BOOL_NAO  : 'nao';

ATRIB        : '<-';
DELIM        : ':';
VIRGULA      : ',';
ABREPAR      : '(';
FECHAPAR     : ')';
PONTO        : '.';

// Identificadores e Números
IDENT    : [a-zA-Z_] [a-zA-Z0-9_]* ;
NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT  : [0-9]+ ;

CADEIA : '"' (~["\\\r\n] | ESC_SEQ)* '"' ;
fragment ESC_SEQ : '\\' . ; 

WS : [ \t\r\n]+ -> skip ;
COMENTARIO: '{' .*? '}' -> skip;

// Erros léxicos básicos
CADEIA_NAO_FECHADA : '"' (~["\r\n])* ;
INVALIDO : . ;

// REGRAS SINTÁTICAS

programa
    : ALGORITMO listaDeclaracoes corpo FIM_ALGORITMO
    ;

listaDeclaracoes
    : DECLARE (declaracao)+
    | // vazio
    ;

declaracao
    : IDENT (VIRGULA IDENT)* DELIM tipo
    ;

tipo
    : INTEIRO | REAL | LITERAL | LOGICO
    ;

corpo
    : (comando)*
    ;

comando
    : comandoLeia
    | comandoEscreva
    | comandoAtribuicao
    | comandoCondicao
    | comandoRepeticao
    ;

comandoLeia
    : LEIA ABREPAR IDENT (VIRGULA IDENT)* FECHAPAR
    ;

comandoEscreva
    : ESCREVA ABREPAR expressao (VIRGULA expressao)* FECHAPAR
    ;

comandoAtribuicao
    : IDENT ATRIB expressao
    ;

comandoCondicao
    : SE expressao ENTAO corpo (SENAO corpo)? FIM_SE
    ;

comandoRepeticao
    : ENQUANTO expressao FACA corpo FIM_ENQUANTO
    ;

// Hierarquia de Expressões (Trata precedência)
expressao
    : termoRelacional (OP_BOOL_OU termoRelacional)*
    ;

termoRelacional
    : expressaoAritmetica (OP_RELAC expressaoAritmetica)?
    ;

expressaoAritmetica
    : termoAritmetico (OP_ARIT_SOMA termoAritmetico)*
    ;

termoAritmetico
    : fatorAritmetico (OP_ARIT_MULT fatorAritmetico)*
    ;

fatorAritmetico
    : NUM_INT
    | NUM_REAL
    | IDENT
    | CADEIA
    | VERDADEIRO
    | FALSO
    | ABREPAR expressao FECHAPAR
    ;