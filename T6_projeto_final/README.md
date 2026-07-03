
# FichaTreino (FT) — Compilador para fichas de treino de musculação

Trabalho 6 da disciplina **Construção de Compiladores** (Prof. Daniel Lucrédio).

Integrantes:
-   Karys Cristina da Silva Barbosa - 811871
-   Nathália Brasilino Gimenes - 812076
-   Pedro Henrique Ghiotto - 812115

## 1. O que é a linguagem FichaTreino?

**FichaTreino (FT)** é uma linguagem declarativa de domínio específico (DSL) para descrever
**fichas de treino de musculação** de um ou mais alunos. Cada aluno pode ter dados pessoais
opcionais (idade, peso, objetivo) e um ou mais **treinos** (ex: Treino A, Treino B), e cada
treino contém uma lista de **exercícios**, cada um com número de séries e repetições.

### Exemplo de entrada

```
aluno Nathalia {
    idade 22
    peso 58
    objetivo hipertrofia

    treino A {
        exercicio Supino {
            series 4
            repeticoes 10
        }
        exercicio TricepsPulley {
            series 3
            repeticoes 12
        }
    }
    treino B {
        exercicio Agachamento {
            series 4
            repeticoes 12
        }
        exercicio LegPress {
            series 4
            repeticoes 15
        }
    }
}
```

### Exemplo de saída (HTML renderizado)

O compilador gera um arquivo `.html` contendo, para cada aluno, seus dados (se informados), uma tabela por treino com os exercícios (séries, repetições e volume) e os totais do treino, além de um resumo geral no
final da página com o total de alunos, treinos, exercícios e volume.

## 2. Gramática (análise léxica/sintática)

A gramática completa está em [`FichaTreino.g4`](FichaTreino.g4), escrita para **ANTLR4**.
Estrutura geral:

```
programa     : aluno+ EOF ;
aluno        : 'aluno' ID '{' atributoAluno* treino+ '}' ;
atributoAluno: 'idade' INT | 'peso' numero | 'objetivo' ID ;
treino       : 'treino' ID '{' exercicio* '}' ;
exercicio    : 'exercicio' ID '{' 'series' valorInt 'repeticoes' valorInt '}' ;
```

Os atributos `idade`, `peso` e `objetivo` do aluno são **opcionais**.



## 3. Como compilar

Pré-requisitos: **Java 11+** e **ANTLR4** (jar `antlr4-4.13.1-complete.jar` ou similar).

### Opção A — usando Maven

```bash
mvn clean package
java -jar target/fichatreino-1.0-jar-with-dependencies.jar exemplos/ok1.txt
```

O plugin `antlr4-maven-plugin` configurado no `pom.xml` gera automaticamente as classes
`FichaTreinoLexer`, `FichaTreinoParser`, etc., a partir de `FichaTreino.g4`.

### Opção B — manualmente (sem Maven)

```bash
# 1. Gerar o lexer/parser a partir da gramática
java -jar antlr4-4.13.1-complete.jar -Dlanguage=Java -visitor -o generated FichaTreino.g4

# 2. Adicionar "package ft;" no topo de cada arquivo gerado e copiar para src/main/java/ft/

# 3. Compilar
javac -cp antlr4-4.13.1-complete.jar -d build $(find src/main/java -name "*.java")

# 4. Executar
java -cp build:antlr4-4.13.1-complete.jar ft.Main exemplos/ok1.txt
```

## 4. Como executar

```bash
java -cp build:antlr4-4.13.1-complete.jar ft.Main <caminho-do-arquivo.txt> [saida.html]
```

Códigos de saída:

- `0`: sucesso, HTML gerado;
- `1`: erro de leitura/escrita de arquivo;
- `2`: erro léxico/sintático (mensagens na saída de erro);
- `3`: erro(s) semântico(s) (mensagens na saída de erro).

## 5. Exemplos de teste

A pasta [`exemplos/`](exemplos/) contém:

| Arquivo                          | O que demonstra                                              |
|-----------------------------------|---------------------------------------------------------------|
| `ok1.txt`                          | Ficha válida com múltiplos alunos, treinos e exercícios       |
| `erro_sintatico.txt`               | Erro léxico/sintático (chave `}` faltando)                     |
| `erro_exercicio_repetido.txt`      | Exercício duplicado no mesmo treino                            |
| `erro_series_zero.txt`             | `series` igual a zero                                          |
| `erro_repeticoes_negativa.txt`     | `repeticoes` negativa                                          |
| `erro_treino_vazio.txt`            | Treino sem exercícios                                          |
| `erro_extra.txt`                   | Treino duplicado + objetivo inválido (verificações extras)     |

Exemplo de Saída:
Ok1.txt

![image alt](https://github.com/akarys2304/Compiladores_2026.1/blob/59c8c162a072987194e2642237405584c0b60260/T6_projeto_final/1000512413.jpg)


## 5. Tratamento de erros

| Caso                     | Descrição                                                                                                                                                           |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Exercícios repetidos** | Impede que um mesmo exercício seja cadastrado mais de uma vez dentro do mesmo treino.                                                                               |
| **Treinos duplicados**   | Verifica se um aluno possui dois ou mais treinos com o mesmo nome.                                                                                                  |
| **Valores inválidos**    | Garante que `series` e `repeticoes` sejam maiores que zero.                                                                                                         |
| **Treino vazio**         | Detecta treinos criados sem nenhum exercício.                                                                                                                       |
| **Objetivo inválido**    | Valida o atributo opcional `objetivo`, que deve possuir um dos seguintes valores: `hipertrofia`, `resistencia`, `emagrecimento`, `condicionamento` ou `mobilidade`. |
| **Erros sintáticos**     | Detecta problemas na estrutura da linguagem, como ausência de chaves, parênteses ou outros elementos obrigatórios da gramática.                                     |


Obs: O diretório exemplos/ do repositório contém arquivos para demonstrar tanto o funcionamento correto da linguagem quanto os diferentes tipos de erros tratados pelo compilador.
