grammar Jander;

// -------------------
// LÉXICO
// -------------------

ALGORITMO     : 'algoritmo';
DECLARE       : 'declare';
LEIA          : 'leia';
ESCREVA       : 'escreva';
FIM_ALGORITMO : 'fim_algoritmo';

SE            : 'se';
ENTAO         : 'entao';
SENAO         : 'senao';
FIM_SE        : 'fim_se';

ENQUANTO      : 'enquanto';
FIM_ENQUANTO  : 'fim_enquanto';
FACA          : 'faca';

// 🔥 NOVOS TOKENS (necessários)
PARA          : 'para';
ATE           : 'ate';
FIM_PARA      : 'fim_para';

INTEIRO       : 'inteiro';
REAL          : 'real';
LITERAL       : 'literal';
LOGICO        : 'logico';
VERDADEIRO    : 'verdadeiro';
FALSO         : 'falso';

// Operadores e símbolos
ATRIB        : '<-';
DELIM        : ':';
VIRGULA      : ',';
ABREPAR      : '(';
FECHAPAR     : ')';

OP_ARIT_SOMA : '+' | '-';
OP_ARIT_MULT : '*' | '/';
OP_RELAC     : '>' | '<' | '>=' | '<=' | '<>' | '=';
OP_BOOL_E    : 'e';
OP_BOOL_OU   : 'ou';
OP_BOOL_NAO  : 'nao';

// Identificadores e números
IDENT    : [a-zA-Z_] [a-zA-Z0-9_]* ;
NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT  : [0-9]+ ;

// Cadeias
CADEIA : '"' (~["\\\r\n] | ESC_SEQ)* '"' ;
fragment ESC_SEQ : '\\' . ;

// Comentários e espaços
WS : [ \t\r\n]+ -> skip ;
COMENTARIO : '{' ~('}'|'\n')* '}' -> skip ;
COMENTARIO_NAO_FECHADO: '{' ~('}'|'\n')* '\n';

// Erros léxicos
CADEIA_NAO_FECHADA : '"' (~["\r\n])* '\n';
ERRO : . ;

// -------------------
// SINTÁTICO
// -------------------

programa
    : declaracoes ALGORITMO corpo FIM_ALGORITMO EOF
    ;

// mantém simples
declaracoes
    : (DECLARE declaracao)*
    ;

declaracao
    : IDENT (VIRGULA IDENT)* DELIM tipo
    ;

tipo
    : INTEIRO
    | REAL
    | LITERAL
    | LOGICO
    ;

// 🔥 CORREÇÃO IMPORTANTE: permitir declare dentro do corpo
corpo
    : (DECLARE declaracao | comando)*
    ;

// -------------------
// COMANDOS
// -------------------

comando
    : comandoLeia
    | comandoEscreva
    | comandoAtribuicao
    | comandoCondicao
    | comandoRepeticao
    | comandoPara
    ;

// 🔥 NOVO (mínimo necessário)
comandoPara
    : PARA IDENT ATRIB expressao (ATE expressao)? FACA comando* FIM_PARA
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
    : SE expressao ENTAO comando* (SENAO comando*)? FIM_SE
    ;

comandoRepeticao
    : ENQUANTO expressao FACA comando* FIM_ENQUANTO
    ;

// -------------------
// EXPRESSÕES
// -------------------

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
