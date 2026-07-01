# FichaTreino (FT) — Compilador para fichas de treino de musculação

Trabalho 6 da disciplina **Construção de Compiladores** (Prof. Daniel Lucrédio).

## 1. O que é a linguagem FichaTreino?

**FichaTreino (FT)** é uma linguagem declarativa de domínio específico (DSL) para descrever
**fichas de treino de musculação** de um ou mais alunos. Cada aluno pode ter dados pessoais
opcionais (idade, peso, objetivo) e um ou mais **treinos** (ex: Treino A, Treino B), e cada
treino contém uma lista de **exercícios**, cada um com número de séries e repetições.

O compilador lê um arquivo `.ft`, valida a estrutura e o conteúdo da ficha e, se tudo estiver
correto, gera uma **página HTML** com a ficha de treino formatada, incluindo estatísticas como
volume (séries × repetições) por exercício, por treino e um resumo geral.

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

O compilador gera um arquivo `.html` (por padrão, ao lado do `.ft` de entrada, com o mesmo
nome). A página exibe, para cada aluno, seus dados (se informados), uma tabela por treino com
os exercícios (séries, repetições e volume) e os totais do treino, além de um resumo geral no
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

## 3. Análise semântica

Além do que a gramática garante, o compilador realiza as seguintes verificações:

1. **Exercício duplicado**: um nome de exercício não pode se repetir dentro do mesmo treino.
2. **`series > 0`**: o número de séries de um exercício deve ser um inteiro maior que zero.
3. **`repeticoes > 0`**: o número de repetições deve ser um inteiro maior que zero.
4. **Treino não vazio**: todo treino declarado deve conter pelo menos um exercício.

Verificações extras (bônus):

5. **Treino duplicado**: o nome de um treino (ex: "A") não pode se repetir para o mesmo aluno.
6. **Objetivo válido**: se o atributo `objetivo` for informado, deve ser um dos valores
   reconhecidos: `hipertrofia`, `resistencia`, `emagrecimento`, `condicionamento`, `mobilidade`.

Se algum erro semântico for encontrado, o compilador lista **todos** os erros encontrados,
indicando a linha de cada um, e não gera a ficha de saída.

## 4. Geração / Interpretação

Após passar pela análise semântica sem erros, o compilador **interpreta** a ficha e gera uma
página HTML organizada, calculando:

- Volume (séries × repetições) de cada exercício;
- Total de séries e volume total de cada treino;
- Resumo geral (total de alunos, treinos, exercícios e volume).

## 5. Como compilar

Pré-requisitos: **Java 11+** e **ANTLR4** (jar `antlr4-4.13.1-complete.jar` ou similar).

### Opção A — usando Maven

```bash
mvn clean package
java -jar target/fichatreino-1.0-jar-with-dependencies.jar exemplos/ok1.ft
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
java -cp build:antlr4-4.13.1-complete.jar ft.Main exemplos/ok1.ft
```

## 6. Como executar

```bash
java -cp build:antlr4-4.13.1-complete.jar ft.Main <caminho-do-arquivo.ft> [saida.html]
```

Se `[saida.html]` não for informado, o arquivo HTML é gravado ao lado do `.ft` de entrada,
com o mesmo nome (ex: `ok1.ft` → `ok1.html`). Basta abrir o `.html` gerado em um navegador.

Códigos de saída:

- `0`: sucesso, HTML gerado;
- `1`: erro de leitura/escrita de arquivo;
- `2`: erro léxico/sintático (mensagens na saída de erro);
- `3`: erro(s) semântico(s) (mensagens na saída de erro).

## 7. Exemplos de teste

A pasta [`exemplos/`](exemplos/) contém:

| Arquivo                          | O que demonstra                                              |
|-----------------------------------|---------------------------------------------------------------|
| `ok1.ft`                          | Ficha válida com múltiplos alunos, treinos e exercícios       |
| `erro_sintatico.ft`               | Erro léxico/sintático (chave `}` faltando)                     |
| `erro_exercicio_repetido.ft`      | Exercício duplicado no mesmo treino                            |
| `erro_series_zero.ft`             | `series` igual a zero                                          |
| `erro_repeticoes_negativa.ft`     | `repeticoes` negativa                                          |
| `erro_treino_vazio.ft`            | Treino sem exercícios                                          |
| `erro_extra.ft`                   | Treino duplicado + objetivo inválido (verificações extras)     |

## 8. Estrutura do código-fonte

```
T6/
├── FichaTreino.g4              # gramática ANTLR4
├── pom.xml                     # build Maven
├── src/main/java/ft/
│   ├── Main.java               # ponto de entrada do compilador
│   ├── ModeloBuilder.java       # visitor: AST -> modelo de domínio
│   ├── SemanticChecker.java     # análise semântica (4+ verificações)
│   ├── ErroSemantico.java       # representação de um erro semântico
│   ├── FichaGenerator.java       # interpretação / geração da ficha
│   └── model/
│       ├── Aluno.java
│       ├── Treino.java
│       └── Exercicio.java
└── exemplos/                    # casos de teste (.ft)
```

## 9. Origem e diferenças

A linguagem FichaTreino é uma criação original para esta disciplina, não derivada de nenhuma
linguagem ou DSL existente.
