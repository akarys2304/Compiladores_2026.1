grammar FichaTreino;

@header {
package ft;
}


// REGRAS SINTÁTICAS 

programa : aluno+ EOF ;

aluno : 'aluno' ID '{' atributoAluno* treino+ '}' ;

atributoAluno
    : 'idade' INT                 # AtribIdade
    | 'peso' numero                # AtribPeso
    | 'objetivo' ID                 # AtribObjetivo
    ;


treino : 'treino' ID '{' exercicio* '}' ;
exercicio : 'exercicio' ID '{' 'series' valorInt 'repeticoes' valorInt '}' ;
valorInt : ('-')? INT ;

numero : ('-')? (INT | FLOAT) ;

// REGRAS LÉXICAS 

ID      : [a-zA-Z_][a-zA-Z0-9_]* ;
INT     : [0-9]+ ;
FLOAT   : [0-9]+ '.' [0-9]+ ;

WS        : [ \t\r\n]+ -> skip ;
COMMENT   : '//' ~[\r\n]* -> skip ;
MLCOMMENT : '/*' .*? '*/' -> skip ;
