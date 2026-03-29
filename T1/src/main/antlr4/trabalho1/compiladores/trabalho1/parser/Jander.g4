lexer grammar Jander;

PALAVRA_CHAVE: 
    'algoritmo' | 'declare' | 'leia' | 'escreva' | 'fim_algoritmo' | 
    'se' | 'senao' | 'fim_se' | 'para' | 'fim_para' | 'enquanto' | 
    'fim_enquanto' | 'faca' | 'fim_registro' | 'ate' | 'procedimento' | 
    'fim_procedimento' | 'registro' | 'var' | 'constante' | 'tipo' | 
    'retorne' | 'entao' | 'inteiro' | 'real' | 'literal' | 'logico' | 
    'verdadeiro' | 'falso' | 'e' | 'ou' | 'nao' | 'funcao' | 'fim_funcao' | 
    'caso' | 'seja' | 'fim_caso';

ARIT     : '+' | '-' | '*' | '/' | '%' ;
RELAC    : '>' | '<' | '>=' | '<=' | '<>' | '=' ;
ATRIB    : '<-' ;
PONTEIRO : '^' ;
ENDERECO : '&' ;
DELIM    : ':' ;
VIRGULA  : ',' ;
ABREPAR  : '(' ;
FECHAPAR : ')' ;
ABRECOL  : '[' ;
FECHACOL : ']' ;
INTERVALO: '..' ;
PONTO    : '.' ;

CADEIA : '"' (~["\\\r\n] | ESC_SEQ)* '"' ;
fragment ESC_SEQ : '\\' . ; 

NUM_REAL : [0-9]+ '.' [0-9]+ ;
NUM_INT  : [0-9]+ ;
IDENT    : [a-zA-Z_] [a-zA-Z0-9_]* ;

WS : [ \t\r\n]+ -> skip ;

COMENTARIO : '{' .*? '}' -> skip ;

COMENTARIO_NAO_FECHADO : '{' .*? ;

CADEIA_NAO_FECHADA : '"' (~["\r\n])* ;

INVALIDO : . ;